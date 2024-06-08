package com.zerobase.reserve.service;

import com.zerobase.domain.dto.ReserveResponseDto;
import com.zerobase.domain.redis.RedisClient;
import com.zerobase.domain.redis.Waiting;
import com.zerobase.domain.repository.CustomerRepository;
import com.zerobase.domain.requestForm.ReserveRequestForm;
import com.zerobase.domain.exception.CustomException;
import com.zerobase.domain.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReserveService {

    private final RedisClient redisClient;
    private final CustomerRepository customerRepository;

    @Transactional
    public ReserveResponseDto addWaiting(ReserveRequestForm reserveRequestForm) {
        Waiting.Customer waitingCustomer =
                Waiting.Customer.from(reserveRequestForm);

        Waiting waiting =
                redisClient.get(reserveRequestForm.getStoreId(), Waiting.class);

        //첫 waiting 이라면 고객 추가
        if (waiting == null) {
            waiting = new Waiting();
            waiting.getCustomerList().add(waitingCustomer);
        }

        validateCustomer(reserveRequestForm.getCustomerId(), waiting);

        waiting.getCustomerList().add(waitingCustomer);

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

        Optional<Waiting.Customer> waitingCustomer = waiting.getCustomerList()
                .stream()
                .filter(customer -> customer.getName().equals(customerName) &&
                        customer.getPhone().equals(customerPhone))
                .findFirst();
        if (waitingCustomer.isPresent()) {
            waitingCustomer.get().setConfirm(true);
            redisClient.put(storeId, waiting);
        } else {
            throw new CustomException(ErrorCode.NOT_FOUND_BOOK);
        }

        return ReserveResponseDto.from(waitingCustomer.get());
    }

    private void validateCustomer(Long customerId, Waiting waiting) {
        //존재여부 확인
        if(!customerRepository.existsById(customerId)) {
            throw new CustomException(ErrorCode.NOT_FOUND_USER);
        }
        //이전에 같은 고객이 있는지?(이미 예약한 이력이 있으면 예약 거부)
        boolean booked = waiting.getCustomerList().stream()
                .noneMatch(customer -> customer.getId().equals(customerId));

        if (booked) {
            throw new CustomException(ErrorCode.ALREADY_BOOKED_CUSTOMER);
        }
    }
}
