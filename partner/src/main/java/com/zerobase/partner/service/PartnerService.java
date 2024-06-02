package com.zerobase.partner.service;

import com.zerobase.partner.domain.dto.PartnerDto;
import com.zerobase.partner.domain.model.PartnerEntity;
import com.zerobase.partner.domain.repository.PartnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PartnerService implements UserDetailsService {
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

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {
        return partnerRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found" + email));
    }
}
