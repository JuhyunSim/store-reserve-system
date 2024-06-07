package com.zerobase.partner.service;

import com.zerobase.domain.dto.CustomerDto;
import com.zerobase.domain.entity.CustomerEntity;
import com.zerobase.domain.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService implements UserDetailsService {

    private final CustomerRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {
            return customerRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }

    public CustomerDto getCustomerInfo(Long partnerId, String email) {
        CustomerEntity customerEntity = customerRepository.findByIdAndEmail(partnerId, email).orElseThrow(
                () -> new RuntimeException("존재하지 않는 파트너 입니다.")
        );
        return CustomerDto.from(customerEntity);
    }
}
