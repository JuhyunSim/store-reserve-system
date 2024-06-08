package com.zerobase.domain.requestForm;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReserveRequestForm {

    private Long storeId;
    private Long customerId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
}
