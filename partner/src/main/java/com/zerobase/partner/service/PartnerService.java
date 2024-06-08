package com.zerobase.partner.service;

import com.zerobase.domain.dto.PartnerDto;
import com.zerobase.domain.entity.PartnerEntity;
import com.zerobase.domain.repository.PartnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PartnerService {
    private final PartnerRepository partnerRepository;

    public PartnerDto getPartnerInfo(Long partnerId, String email) {
        PartnerEntity partnerEntity = partnerRepository.findByIdAndEmail(partnerId, email).orElseThrow(
                () -> new RuntimeException("존재하지 않는 파트너 입니다.")
        );
        return PartnerDto.from(partnerEntity);
    }

    public Optional<PartnerEntity> findByIdAndEmail(Long id, String email) {
        return partnerRepository.findById(id).stream()
                .filter(customer -> customer.getEmail().equals(email))
                .findFirst();
    }
}
