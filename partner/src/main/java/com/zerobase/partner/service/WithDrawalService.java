package com.zerobase.partner.service;

import com.zerobase.domain.dto.WithDrawalDto;
import com.zerobase.domain.entity.CustomerEntity;
import com.zerobase.domain.entity.PartnerEntity;
import com.zerobase.domain.exception.CustomException;
import com.zerobase.domain.exception.ErrorCode;
import com.zerobase.domain.repository.CustomerRepository;
import com.zerobase.domain.repository.PartnerRepository;
import com.zerobase.domain.requestForm.WithDrawalForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WithDrawalService {

    private final PartnerRepository partnerRepository;
    private final CustomerRepository customerRepository;

    @Transactional
    public WithDrawalDto deletePartner(Long partnerId, WithDrawalForm withDrawalForm) {
        PartnerEntity partnerEntity = validatePartner(partnerId, withDrawalForm);

        partnerRepository.deleteByIdAndEmail(
                partnerEntity.getId(), partnerEntity.getEmail()
        );

        return WithDrawalDto.of(
                partnerEntity.getEmail(), partnerEntity.getName()
        );
    }

    @Transactional
    public WithDrawalDto deleteCustomer(Long customerId, WithDrawalForm withDrawalForm) {
        CustomerEntity customerEntity =
                validateCustomer(customerId, withDrawalForm);

        customerRepository.deleteByIdAndEmail(
                customerEntity.getId(), customerEntity.getEmail());

        return WithDrawalDto.of(
                customerEntity.getEmail(), customerEntity.getName()
        );
    }

    private CustomerEntity validateCustomer(Long customerId, WithDrawalForm withDrawalForm) {
        CustomerEntity customerEntity = customerRepository.findByIdAndEmail(customerId, withDrawalForm.getEmail()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER)
        );

        if (!customerEntity.getPassword().equals(withDrawalForm.getPassword())) {
            throw new CustomException(ErrorCode.CHECK_PASSWORD);
        }
        return customerEntity;
    }

    private PartnerEntity validatePartner(
            Long partnerId, WithDrawalForm withDrawalForm
    ) {
        PartnerEntity partnerEntity = partnerRepository.findByIdAndEmail(partnerId, withDrawalForm.getEmail()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER)
        );

        if (!partnerEntity.getPassword().equals(withDrawalForm.getPassword())) {
            throw new CustomException(ErrorCode.CHECK_PASSWORD);
        }
        return partnerEntity;
    }
}
