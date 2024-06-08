package com.zerobase.domain.redis;

import com.zerobase.domain.entity.BaseEntity;
import com.zerobase.domain.requestForm.ReserveRequestForm;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.envers.AuditOverride;
import org.springframework.data.redis.core.RedisHash;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@RedisHash("waiting")
public class Waiting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long partnerId;
    private Long storeId;
    private List<Customer> customerList = new ArrayList<>();

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @AuditOverride
    public static class Customer extends BaseEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private Long customerId;
        private String name;
        private String phone;
        private boolean confirm;
        //createdDate -> 예약시간

        public static Customer from(ReserveRequestForm reserveRequestForm) {
            return Customer.builder()
                    .customerId(reserveRequestForm.getCustomerId())
                    .name(reserveRequestForm.getCustomerName())
                    .phone(reserveRequestForm.getCustomerPhone())
                    .confirm(false)
                    .build();
        }
    }

}
