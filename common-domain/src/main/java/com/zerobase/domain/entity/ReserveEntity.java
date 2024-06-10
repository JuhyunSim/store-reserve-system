package com.zerobase.domain.entity;

import com.zerobase.domain.constant.Accepted;
import com.zerobase.domain.requestForm.ReserveRequestForm;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;

@Entity(name = "reservation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@AuditOverride(forClass = BaseEntity.class)
@Audited
public class ReserveEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long storeId;
    private Long partnerId;
    private String storeName;
    private Long customerId;
    private String customerName;
    private Long waitingNumber;
    private String customerPhone;
    private LocalDateTime reserveTime;
    private boolean confirm;
    private Accepted accepted;

    public static ReserveEntity from(ReserveRequestForm reserveRequestForm) {
        return ReserveEntity.builder()
                .storeId(reserveRequestForm.getStoreId())
                .partnerId(reserveRequestForm.getPartnerId())
                .customerId(reserveRequestForm.getCustomerId())
                .customerName(reserveRequestForm.getCustomerName())
                .waitingNumber(-1L)
                .customerPhone(reserveRequestForm.getCustomerPhone())
                .reserveTime(reserveRequestForm.getReserveTime())
                .confirm(false)
                .accepted(Accepted.WAITING)
                .build();
    }
}
