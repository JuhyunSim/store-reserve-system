package com.zerobase.partner.domain;

import com.zerobase.partner.domain.model.PartnerEntity;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpForm {

    private String email;
    private String password;
    private String name;
    private String confirmPassword;
    private String phone;
    private String registerNumber;

    public PartnerEntity from() {
        return PartnerEntity.builder()
                .email(this.email)
                .name(this.name)
                .phone(this.phone)
                .registerNumber(this.registerNumber)
                .verify(false)
                .build();
    }

}
