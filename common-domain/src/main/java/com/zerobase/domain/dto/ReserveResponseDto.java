package com.zerobase.domain.dto;

import com.zerobase.domain.redis.Waiting;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReserveResponseDto {
    private Long customerId;
    private String customerName;
    private String waitingNum;
    private String phone;
    private boolean confirm;


    public static ReserveResponseDto from(Waiting.Customer waitingCustomer) {
        return ReserveResponseDto.builder()
                .customerId(waitingCustomer.getCustomerId())
                .customerName(waitingCustomer.getName())
                .phone(waitingCustomer.getPhone())
                .confirm(waitingCustomer.isConfirm())
                .build();
    }
}
