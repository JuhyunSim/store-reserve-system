package com.zerobase.partner.domain.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartnerDto {
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
}
