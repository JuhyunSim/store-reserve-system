package com.zerobase.partner.application;

import com.zerobase.domain.entity.CustomerEntity;
import com.zerobase.domain.security.common.UserType;
import com.zerobase.domain.security.config.JwtAuthProvider;
import com.zerobase.domain.requestForm.SignInForm;
import com.zerobase.domain.entity.PartnerEntity;
import com.zerobase.partner.service.SignInService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
@RequiredArgsConstructor
public class SignInApplication {
    private final SignInService signInService;
    private final JwtAuthProvider jwtAuthProvider;

    public String signInPartner(SignInForm signInForm)
            throws InvalidAlgorithmParameterException,
            NoSuchPaddingException,
            IllegalBlockSizeException,
            NoSuchAlgorithmException,
            BadPaddingException,
            InvalidKeyException {
        String email = signInForm.getEmail();
        String password = signInForm.getPassword();
        //이메일이 리포지토리에 있는지 확인
        //signInForm의 비밀번호와 등록된 비밀번호가 일치하는지 확인
        //verify==true 확인
        PartnerEntity partnerEntity =
                signInService.partnerFindEmailAndComparePassword(signInForm);

        //jwt 토큰 발행 (토큰을 리턴함)
        return jwtAuthProvider.generateToken(
                partnerEntity.getEmail(),
                partnerEntity.getId(),
                UserType.PARTNER);

    }

    public String signInCustomer(SignInForm signInForm)
            throws InvalidAlgorithmParameterException,
            NoSuchPaddingException,
            IllegalBlockSizeException,
            NoSuchAlgorithmException,
            BadPaddingException,
            InvalidKeyException {
        String email = signInForm.getEmail();
        String password = signInForm.getPassword();
        //이메일이 리포지토리에 있는지 확인
        //signInForm의 비밀번호와 등록된 비밀번호가 일치하는지 확인
        //verify==true 확인
        CustomerEntity customerEntity =
                signInService.customerFindEmailAndComparePassword(signInForm);

        //jwt 토큰 발행 (토큰을 리턴함)
        return jwtAuthProvider.generateToken(
                customerEntity.getEmail(),
                customerEntity.getId(),
                UserType.CUSTOMER);

    }
}
