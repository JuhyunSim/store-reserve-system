package com.zerobase.domain.security.service;

import com.zerobase.domain.repository.PartnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityPartnerService implements UserDetailsService {

    private final PartnerRepository partnerRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return partnerRepository.findByEmail(email.toLowerCase()).orElseThrow(
                        () -> new UsernameNotFoundException(email)
                );
    }
}
