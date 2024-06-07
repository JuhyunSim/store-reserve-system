package com.zerobase.domain.dto;

import com.zerobase.reserve.domain.redis.Waiting;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReserveDto {
    private Long customerId;
    private String customerName;
    private String waitingNum;
    private String phone;
    private boolean confirm;


    public static ReserveDto from(Waiting.Customer waitingCustomer) {
        return ReserveDto.builder()
                .customerId(waitingCustomer.getCustomerId())
                .customerName(waitingCustomer.getName())
                .phone(waitingCustomer.getPhone())
                .confirm(false)
                .build();
    }
}
