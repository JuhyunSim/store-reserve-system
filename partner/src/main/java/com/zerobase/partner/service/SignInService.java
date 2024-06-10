package com.zerobase.partner.service;

import com.zerobase.domain.entity.CustomerEntity;
import com.zerobase.domain.entity.PartnerEntity;
import com.zerobase.domain.exception.CustomException;
import com.zerobase.domain.exception.ErrorCode;
import com.zerobase.domain.repository.CustomerRepository;
import com.zerobase.domain.repository.PartnerRepository;
import com.zerobase.domain.requestForm.SignInForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignInService {

    private final PartnerRepository partnerRepository;
    private final CustomerRepository customerRepository;

    //partner
    private PartnerEntity partnerFindByEmail(SignInForm signInForm) {
        return partnerRepository.findByEmail(signInForm.getEmail()).orElseThrow(() ->
                new RuntimeException("존재하지 않는 회원입니다."));
    }

    public PartnerEntity partnerFindEmailAndComparePassword(SignInForm signInForm) {
        PartnerEntity partnerEntity = partnerFindByEmail(signInForm);
        if(!partnerEntity.getPassword().equals(signInForm.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }
        if (!partnerEntity.isVerify()) {
            throw new RuntimeException("인증되지 않은 회원입니다.");
        }

        return partnerEntity;
    }

    //customer
    private CustomerEntity customerFindByEmail(SignInForm signInForm) {
        return customerRepository.findByEmail(signInForm.getEmail()).orElseThrow(() ->
                new RuntimeException("존재하지 않는 회원입니다."));
    }

    public CustomerEntity customerFindEmailAndComparePassword(SignInForm signInForm) {
        CustomerEntity customerEntity = customerFindByEmail(signInForm);
        if(!customerEntity.getPassword().equals(signInForm.getPassword())) {
            throw new CustomException(ErrorCode.CHECK_PASSWORD);
        }
        if (!customerEntity.isVerify()) {
            throw new CustomException(ErrorCode.NOT_VERIFIED);
        }

        return customerEntity;
    }
}
