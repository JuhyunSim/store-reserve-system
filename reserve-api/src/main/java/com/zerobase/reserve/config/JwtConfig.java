package com.zerobase.reserve.config;

import com.zerobase.domain.repository.CustomerRepository;
import com.zerobase.domain.repository.PartnerRepository;
import com.zerobase.partner.domain.repository.CustomerRepository;
import com.zerobase.partner.domain.repository.PartnerRepository;
import com.zerobase.partner.security.config.JwtAuthProvider;
import com.zerobase.partner.service.PartnerService;
import com.zerobase.partner.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class JwtConfig {

    private final PartnerRepository partnerRepository;;
    private final CustomerRepository customerRepository;

    @Bean
    public PartnerService partnerService(PartnerRepository partnerRepository) {
        return new PartnerService(partnerRepository);
    }

    @Bean
    public CustomerService customerService(CustomerRepository customerRepository) {
        return new CustomerService(customerRepository);
    }

    @Bean
    public JwtAuthProvider jwtAuthProvider(
            PartnerService partnerService, CustomerService customerService
    ) {
        return new JwtAuthProvider(partnerService, customerService);
    }
}
