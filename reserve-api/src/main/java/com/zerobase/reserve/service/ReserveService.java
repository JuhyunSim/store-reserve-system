package com.zerobase.reserve.service;

import com.zerobase.domain.entity.CustomerEntity;
import com.zerobase.domain.repository.CustomerRepository;
import com.zerobase.reserve.client.RedisClient;
import com.zerobase.domain.requestForm.ReserveForm;
import com.zerobase.domain.dto.ReserveDto;
import com.zerobase.reserve.redis.Waiting;
import com.zerobase.reserve.exception.CustomException;
import com.zerobase.reserve.exception.ErrorCode;
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
    public ReserveDto addWaiting(Long customerId, ReserveForm reserveForm) {

        CustomerEntity customerEntity = customerRepository.findById(customerId)
                .orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER)
        );
        Waiting.Customer waitingCustomer = Waiting.Customer.from(customerEntity);

        Waiting waiting =
                redisClient.get(reserveForm.getStoreId(), Waiting.class);

        //첫 waiting 이라면 고객 추가
        if (waiting == null) {
            waiting = new Waiting();
            waiting.getCustomerList().add(waitingCustomer);
        }

        //이전에 같은 고객이 있는지?(이미 예약한 이력이 있으면 예약 거부)
        boolean booked = waiting.getCustomerList().stream()
                .noneMatch(customer -> customer.getId().equals(customerId));

        if (booked) {
            throw new CustomException(ErrorCode.ALREADY_BOOKED_CUSTOMER);
        }

        waiting.getCustomerList().add(waitingCustomer);

        redisClient.put(reserveForm.getStoreId(), waiting);

        return ReserveDto.from(waitingCustomer);
    }

    @Transactional
    public ReserveDto confirmReserve(Long storeId, String name, String phone) {
        Waiting waiting = redisClient.get(storeId, Waiting.class);
        if (waiting == null) {
            throw new CustomException(ErrorCode.NOT_FOUND_STORE);
        }

        Optional<Waiting.Customer> waitingCustomer = waiting.getCustomerList()
                .stream()
                .filter(customer -> customer.getName().equals(name) &&
                        customer.getPhone().equals(phone))
                .findFirst();
        if (waitingCustomer.isPresent()) {
            waitingCustomer.get().setConfirm(true);
            redisClient.put(storeId, waiting);
        } else {
            throw new CustomException(ErrorCode.NOT_FOUND_BOOK);
        }

        return ReserveDto.from(waitingCustomer.get());
    }
}
