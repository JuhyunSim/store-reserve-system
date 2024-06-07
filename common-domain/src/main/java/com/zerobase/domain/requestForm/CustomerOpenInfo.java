package com.zerobase.domain.requestForm;

import jakarta.persistence.Embeddable;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class CustomerOpenInfo {

    private String name;
    private String email;
    private String phone;
}
