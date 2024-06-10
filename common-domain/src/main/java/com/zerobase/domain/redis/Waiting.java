package com.zerobase.domain.redis;

import com.zerobase.domain.constant.Accepted;
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
    private Long id;
    private Long storeId;

    @Builder.Default
    private List<Customer> customerList = new ArrayList<>();

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Customer {

        private Long id;
        private Long reserveId;
        private String name;
        private String phone;
        private LocalDateTime createdAt;
        private LocalDateTime expireTime;
        private LocalDateTime reserveTime;
        private Long customerId;
        private boolean confirm;
        private Accepted accepted;


        public static Customer from(ReserveRequestForm reserveRequestForm) {
            Waiting.Customer customer = Customer.builder()
                    .customerId(-1L)
                    .name(reserveRequestForm.getCustomerName())
                    .phone(reserveRequestForm.getCustomerPhone())
                    .createdAt(LocalDateTime.now())
                    .reserveTime(reserveRequestForm.getReserveTime())
                    .confirm(false)
                    .accepted(Accepted.WAITING)
                    .build();
            customer.setExpireTime(customer.getReserveTime().minusMinutes(10));
            return customer;
        }
    }

}
