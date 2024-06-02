package com.zerobase.partner.domain;

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
}
