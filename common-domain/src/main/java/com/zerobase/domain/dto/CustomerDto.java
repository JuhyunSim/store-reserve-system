package com.zerobase.domain.dto;

import com.zerobase.domain.entity.CustomerEntity;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDto {
    private Long id;
    private String email;
    private String password;
    private String name;
    private String phone;

    public static CustomerDto from(CustomerEntity customerEntity) {
        return CustomerDto.builder()
                .id(customerEntity.getId())
                .email(customerEntity.getEmail())
                .password(customerEntity.getPassword())
                .name(customerEntity.getName())
                .phone(customerEntity.getPhone())
                .build();
    }

}
