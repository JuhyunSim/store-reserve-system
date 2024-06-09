package com.zerobase.partner.service;

import com.zerobase.domain.dto.CustomerDto;
import com.zerobase.domain.dto.PartnerDto;
import com.zerobase.domain.entity.CustomerEntity;
import com.zerobase.domain.entity.PartnerEntity;
import com.zerobase.domain.repository.CustomerRepository;
import com.zerobase.domain.repository.PartnerRepository;
import com.zerobase.domain.requestForm.SignUpForm;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SignUpService {

    private final PartnerRepository partnerRepository;
    private final CustomerRepository customerRepository;
    private final EntityManager entityManager;

    //partner
    public boolean partnerIsValidEmail(SignUpForm signUpForm) {
        return partnerRepository.findByEmail(signUpForm.getEmail()).isPresent();
    }

    @Transactional
    public PartnerDto savePartnerEntity(PartnerEntity partnerEntity) {
        PartnerEntity saved = partnerRepository.save(partnerEntity);

        log.info("Before flush - partner id: {}", saved.getId());
        entityManager.flush();
        log.info("After flush - partner id: {}", saved.getId());


        return PartnerDto.from(saved);
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


