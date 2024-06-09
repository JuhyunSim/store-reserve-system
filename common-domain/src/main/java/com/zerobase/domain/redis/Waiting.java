package com.zerobase.domain.redis;

import com.zerobase.domain.requestForm.ReserveRequestForm;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;
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
    private Long storeId;
    private List<Customer> customerList = new ArrayList<>();

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Customer {

        private Long id;
        private LocalDateTime createdAt;
        private LocalDateTime expireTime;
        private LocalDateTime reserveTime;
        private Long customerId;
        private String name;
        private String phone;
        private boolean confirm;


        public static Customer from(ReserveRequestForm reserveRequestForm) {
            Waiting.Customer customer = Customer.builder()
                    .customerId(reserveRequestForm.getCustomerId())
                    .name(reserveRequestForm.getCustomerName())
                    .phone(reserveRequestForm.getCustomerPhone())
                    .createdAt(LocalDateTime.now())
                    .reserveTime(reserveRequestForm.getReserveTime())
                    .confirm(false)
                    .build();
            customer.setExpireTime(customer.reserveTime.minusMinutes(10));
            return customer;
        }
    }

}
