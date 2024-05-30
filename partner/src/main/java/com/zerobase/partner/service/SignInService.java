package com.zerobase.partner.service;

import com.zerobase.partner.domain.SignInForm;
import com.zerobase.partner.domain.model.PartnerEntity;
import com.zerobase.partner.domain.repository.PartnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignInService {

    private final PartnerRepository partnerRepository;

    private PartnerEntity findByEmail(SignInForm signInForm) {
        return partnerRepository.findByEmail(signInForm.getEmail()).orElseThrow(() ->
                new RuntimeException("존재하지 않는 회원입니다."));
    }

    public PartnerEntity findEmailAndComparePassword(SignInForm signInForm) {
        PartnerEntity partnerEntity = findByEmail(signInForm);
        if(!partnerEntity.getPassword().equals(signInForm.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }
        if (!partnerEntity.isVerify()) {
            throw new RuntimeException("인증되지 않은 회원입니다.");
        }

        return partnerEntity;
    }
}
