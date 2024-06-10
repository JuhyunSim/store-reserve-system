package com.zerobase.domain.requestForm;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReserveRequestForm {

    private Long storeId;
    private Long customerId;
    private Long partnerId;
    private String customerName;
    private String customerPhone;
    private LocalDateTime reserveTime;
}
