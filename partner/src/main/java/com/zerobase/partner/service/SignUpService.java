package com.zerobase.partner.service;

import com.zerobase.domain.dto.CustomerDto;
import com.zerobase.domain.dto.PartnerDto;
import com.zerobase.domain.entity.CustomerEntity;
import com.zerobase.domain.entity.PartnerEntity;
import com.zerobase.domain.repository.CustomerRepository;
import com.zerobase.domain.repository.PartnerRepository;
import com.zerobase.domain.requestForm.SignUpForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SignUpService {

    private final PartnerRepository partnerRepository;
    private final CustomerRepository customerRepository;

    //partner
    @Transactional
    public boolean partnerIsValidEmail(SignUpForm signUpForm) {
        return partnerRepository.findByEmail(signUpForm.getEmail()).isPresent();
    }

    @Transactional
    public PartnerDto savePartnerEntity(PartnerEntity partnerEntity) {
        return PartnerDto.from(partnerRepository.save(partnerEntity));
    }

    @Transactional
    public PartnerEntity findPartnerByEmail(String email) {
        return partnerRepository.findByEmail(email).orElseThrow(
                () -> new RuntimeException("Invalid Access")
        );
    }

    //customer
    @Transactional
    public boolean customerIsValidEmail(SignUpForm signUpForm) {
        return customerRepository.findByEmail(signUpForm.getEmail()).isPresent();
    }

    @Transactional
    public CustomerDto saveCustomerEntity(CustomerEntity customerEntity) {
        return CustomerDto.from(customerRepository.save(customerEntity));
    }

    @Transactional
    public CustomerEntity findCustomerByEmail(String email) {
        return customerRepository.findByEmail(email).orElseThrow(
                () -> new RuntimeException("Invalid Access")
        );
    }
}


