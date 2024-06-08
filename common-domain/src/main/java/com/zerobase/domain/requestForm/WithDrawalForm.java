package com.zerobase.domain.requestForm;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WithDrawalForm {
    private String email;
    private String password;
}
