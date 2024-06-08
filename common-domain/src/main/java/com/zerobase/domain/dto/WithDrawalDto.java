package com.zerobase.domain.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WithDrawalDto {
    private String email;
    private String name;

    public static WithDrawalDto of(String email, String name) {
        return WithDrawalDto.builder()
                .email(email)
                .name(name)
                .build();
    }
}
