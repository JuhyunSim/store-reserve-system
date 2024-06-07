package com.zerobase.partner.service;

import com.zerobase.domain.requestForm.SignUpForm;
import com.zerobase.domain.dto.CustomerDto;
import com.zerobase.domain.dto.PartnerDto;
import com.zerobase.domain.entity.CustomerEntity;
import com.zerobase.domain.entity.PartnerEntity;
import com.zerobase.partner.domain.repository.CustomerRepository;
import com.zerobase.partner.domain.repository.PartnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignUpService {

    private final PartnerRepository partnerRepository;
    private final CustomerRepository customerRepository;

    //partner
    public boolean partnerIsValidEmail(SignUpForm signUpForm) {
        return partnerRepository.findByEmail(signUpForm.getEmail()).isPresent();
    }

    public PartnerDto savePartnerEntity(PartnerEntity partnerEntity) {
        return PartnerDto.from(partnerRepository.save(partnerEntity));
    }

    public PartnerEntity findPartnerByEmail(String email) {
        return partnerRepository.findByEmail(email).orElseThrow(
                () -> new RuntimeException("Invalid Access")
        );
    }

    //customer
    public boolean customerIsValidEmail(SignUpForm signUpForm) {
        return customerRepository.findByEmail(signUpForm.getEmail()).isPresent();
    }

    public CustomerDto saveCustomerEntity(CustomerEntity customerEntity) {
        return CustomerDto.from(customerRepository.save(customerEntity));
    }

    public CustomerEntity findCustomerByEmail(String email) {
        return customerRepository.findByEmail(email).orElseThrow(
                () -> new RuntimeException("Invalid Access")
        );
    }
}


