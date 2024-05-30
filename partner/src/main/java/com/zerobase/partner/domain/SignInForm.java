package com.zerobase.partner.domain;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignInForm {
    private String email;
    private String password;
}
