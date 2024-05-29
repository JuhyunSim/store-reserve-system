package com.zerobase.partner.domain.model;

import com.zerobase.partner.domain.dto.PartnerDto;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.envers.AuditOverride;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@AuditOverride
public class PartnerEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String password;
    private String name;
    private String phone;
    private String registerNumber;

    private LocalDateTime verifyExpiredAt;
    private String verificationCode;
    private boolean verify;


    public PartnerDto from() {
        return PartnerDto.builder()
                .email(this.email)
                .password("******")
                .name(this.name)
                .phone(this.phone)
                .registerNumber(this.registerNumber)
                .verifyExpiredAt(this.verifyExpiredAt)
                .verificationCode(this.verificationCode)
                .verify(this.verify)
                .createdAt(this.getCreatedAt())
                .lastModifiedAt(this.getLastModifiedAt())
                .build();
    }
}
