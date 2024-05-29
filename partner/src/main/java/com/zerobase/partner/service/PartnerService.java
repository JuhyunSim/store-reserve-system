package com.zerobase.partner.service;

import com.zerobase.partner.domain.SignUpForm;
import com.zerobase.partner.domain.dto.PartnerDto;
import com.zerobase.partner.domain.model.PartnerEntity;
import com.zerobase.partner.domain.repository.PartnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PartnerService {

    private final PartnerRepository partnerRepository;

    public boolean isValidEmail(SignUpForm signUpForm) {
        return partnerRepository.findByEmail(signUpForm.getEmail()).isPresent();
    }

    public PartnerDto savePartnerEntity(PartnerEntity partnerEntity) {
        return partnerRepository.save(partnerEntity).from();
    }

    public PartnerEntity findPartnerByEmail(String email) {
        return partnerRepository.findByEmail(email).orElseThrow(
                () -> new RuntimeException("Invalid Access")
        );
    }
}


