package com.zerobase.partner.domain.dto;

import com.zerobase.partner.domain.model.PartnerEntity;
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
                .build();
    }
}
