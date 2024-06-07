package com.zerobase.domain.dto;

import com.zerobase.domain.entity.PartnerEntity;
import com.zerobase.partner.security.common.UserType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartnerDto {
    private Long id;
    private String email;
    private String password;
    private String name;
    private String phone;
    private String registerNumber;
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;
    private UserType userType;

    private LocalDateTime verifyExpiredAt;
    private String verificationCode;
    private boolean verify;

    public static PartnerDto from(PartnerEntity partnerEntity) {
        return PartnerDto.builder()
                .id(partnerEntity.getId())
                .email(partnerEntity.getEmail())
                .password(partnerEntity.getPassword())
                .name(partnerEntity.getName())
                .phone(partnerEntity.getPhone())
                .registerNumber(partnerEntity.getRegisterNumber())
                .createdAt(partnerEntity.getCreatedAt())
                .lastModifiedAt(partnerEntity.getLastModifiedAt())
                .userType(partnerEntity.getUserType())
                .build();
    }
}
