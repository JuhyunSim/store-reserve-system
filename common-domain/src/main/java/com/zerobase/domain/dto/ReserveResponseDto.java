package com.zerobase.domain.dto;

import com.zerobase.domain.constant.Accepted;
import com.zerobase.domain.entity.ReserveEntity;
import com.zerobase.domain.redis.Waiting;
import com.zerobase.domain.requestForm.ReserveRequestForm;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReserveResponseDto {
    private Long id;
    //관리자번호 제외
    private String storeName;
    private Long customerId;
    private String customerName;
    private Long waitingNumber;
    private String customerPhone;
    private LocalDateTime reserveTime;
    private boolean confirm;
    private Accepted accepted;

    public static ReserveResponseDto from(ReserveEntity reserveEntity) {
        return ReserveResponseDto.builder()
                .id(reserveEntity.getId())
                .customerId(reserveEntity.getCustomerId())
                .customerName(reserveEntity.getCustomerName())
                .customerPhone(reserveEntity.getCustomerPhone())
                .waitingNumber(reserveEntity.getWaitingNumber())
                .reserveTime(reserveEntity.getReserveTime())
                .confirm(reserveEntity.isConfirm())
                .accepted(reserveEntity.getAccepted())
                .build();
    }
}
