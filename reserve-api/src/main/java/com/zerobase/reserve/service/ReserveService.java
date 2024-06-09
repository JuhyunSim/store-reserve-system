package com.zerobase.reserve.service;

import com.zerobase.domain.dto.ReserveResponseDto;
import com.zerobase.domain.exception.CustomException;
import com.zerobase.domain.exception.ErrorCode;
import com.zerobase.domain.redis.RedisClient;
import com.zerobase.domain.redis.Waiting;
import com.zerobase.domain.repository.CustomerRepository;
import com.zerobase.domain.requestForm.ReserveRequestForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReserveService {

    private final RedisClient redisClient;
    private final CustomerRepository customerRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String ID_KEY = "waiting_id";

    @Transactional
    public ReserveResponseDto addWaiting(ReserveRequestForm reserveRequestForm) {
        Waiting.Customer waitingCustomer =
                Waiting.Customer.from(reserveRequestForm);

        Waiting waiting =
                redisClient.get(reserveRequestForm.getStoreId(), Waiting.class);

        //첫 waiting 이라면 고객 추가
        if (waiting == null) {
            waiting = Waiting.builder()
                    .storeId(reserveRequestForm.getStoreId())
                    .customerList(new ArrayList<>())
                    .build();
        } else {
            validateCustomer(reserveRequestForm.getCustomerId(), waiting);
        }
        waitingCustomer.setId(generateWaitingNum());
        waiting.getCustomerList().add(waitingCustomer);
        log.debug("reserveRequestForm --------> {}", reserveRequestForm.getStoreId());
        redisClient.put(reserveRequestForm.getStoreId(), waiting);

        return ReserveResponseDto.from(waitingCustomer);
    }

    @Transactional
    public ReserveResponseDto confirmReserve(
            Long storeId, String customerName, String customerPhone
    ) {
        Waiting waiting = redisClient.get(storeId, Waiting.class);

        if (waiting == null) {
            throw new CustomException(ErrorCode.NOT_FOUND_STORE);
        }
        Waiting.Customer waitingCustomer =
                validateWaitingCustomer(waiting, customerName, customerPhone);
        redisClient.put(storeId, waiting);

        return ReserveResponseDto.from(waitingCustomer);
    }

    private void validateCustomer(Long customerId, Waiting waiting) {
        //존재여부 확인
        if(!customerRepository.existsById(customerId)) {
            throw new CustomException(ErrorCode.NOT_FOUND_USER);
        }
        //이미 예약한 이력이 있으며 예약 유효시간이 지나지 않았다면 예약 거부
        boolean booked = waiting.getCustomerList().stream().filter(
                    customer ->
                            customer.getExpireTime()
                                    .isAfter(LocalDateTime.now())
                )
                .noneMatch(
                        customer ->
                                customer.getCustomerId().equals(customerId)
                );

        if (!booked) {
            throw new CustomException(ErrorCode.ALREADY_BOOKED_CUSTOMER);
        }
    }

    private Long generateWaitingNum() {
        return redisTemplate.opsForValue().increment(ID_KEY, 1);
    }

    private Waiting.Customer validateWaitingCustomer(
            Waiting waiting, String customerName, String customerPhone
    ) {
        //이름, 전화번호 일치 여부 확인
        Waiting.Customer waitingCustomer = waiting.getCustomerList()
                .stream()
                .filter(customer -> customer.getName().equals(customerName) &&
                        customer.getPhone().equals(customerPhone))
                .findFirst().orElseThrow(
                        () -> new CustomException(ErrorCode.NOT_FOUND_RESERVATION)
                );
        //유효시간 확인
        if (waitingCustomer.getExpireTime().isBefore(LocalDateTime.now())) {
            throw new CustomException(ErrorCode.VALID_TIME_EXPIRED);
        }

        waitingCustomer.setConfirm(true);
        return waitingCustomer;
    }

}
