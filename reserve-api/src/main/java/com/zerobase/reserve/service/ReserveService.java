package com.zerobase.reserve.service;

import com.zerobase.domain.constant.Accepted;
import com.zerobase.domain.dto.ReserveResponseDto;
import com.zerobase.domain.entity.ReserveEntity;
import com.zerobase.domain.entity.StoreEntity;
import com.zerobase.domain.exception.CustomException;
import com.zerobase.domain.exception.ErrorCode;
import com.zerobase.domain.redis.RedisClient;
import com.zerobase.domain.redis.Waiting;
import com.zerobase.domain.repository.CustomerRepository;
import com.zerobase.domain.repository.ReserveRepository;
import com.zerobase.domain.repository.StoreRepository;
import com.zerobase.domain.requestForm.ReserveRequestForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReserveService {

    private final RedisClient redisClient;
    private final CustomerRepository customerRepository;
    private final StoreRepository storeRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String ID_KEY = "waiting_id";
    private final ReserveRepository reserveRepository;


    @Transactional
    public ReserveResponseDto addWaiting(ReserveRequestForm reserveRequestForm) {
        Waiting.Customer waitingCustomer =
                Waiting.Customer.from(reserveRequestForm);

        Waiting waiting =
                redisClient.get(reserveRequestForm.getStoreId(), Waiting.class);

        //첫 waiting 이라면 리스트 생성
        if (waiting == null) {
            waiting = Waiting.builder()
                    .storeId(reserveRequestForm.getStoreId())
                    .customerList(new ArrayList<>())
                    .build();
        }
        //대기 추가 가능여부 확인
        validateCustomer(reserveRequestForm, waiting);
        //리스트에 고객 추가(대기번호 발급 x)
        waiting.getCustomerList().add(waitingCustomer);
        //대기명단을 데이터베이스에 추가(redis)
        redisClient.put(reserveRequestForm.getStoreId(), waiting);
        //예약 내역을 데이터베이스에 추가(mysql)
        ReserveEntity reserveEntity =
                ReserveEntity.from(reserveRequestForm);
        ReserveEntity savedReserveEntity =
                reserveRepository.save(reserveEntity);

        return ReserveResponseDto.from(savedReserveEntity);
    }

    @Transactional
    public ReserveResponseDto confirmReserve(
            Long storeId, String customerName, String customerPhone
    ) {
        //waiting list 가져오기
        Waiting waiting = redisClient.get(storeId, Waiting.class);
        //confirm 가능여부 확인 후 confirm
        Waiting.Customer waitingCustomer =
                validateWaitingCustomer(waiting, customerName, customerPhone);
        //데이터베이스 저장
        redisClient.put(storeId, waiting);
        //mysql 저장 정보 수정
        ReserveEntity reserveEntity = reserveRepository.findAllByStoreId(storeId).stream()
                .filter(e ->
                        e.getReserveTime().equals(waitingCustomer.getReserveTime()) &&
                                e.getCustomerName().equals(customerName) &&
                                e.getCustomerPhone().equals(customerPhone))
                .findFirst().orElseThrow(
                        () -> new CustomException(ErrorCode.NOT_FOUND_RESERVATION_HISTORY)
                );
        reserveEntity.setConfirm(true);

        return ReserveResponseDto.from(reserveRepository.save(reserveEntity));
    }

    @Transactional
    public List<ReserveResponseDto> getCustomerReservations(Long customerId) {
        List<ReserveEntity> reservations = reserveRepository.findAllByCustomerId(customerId);

        if (reservations.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND_RESERVATION);
        }

        return reservations.stream()
                .map(ReserveResponseDto::from)
                .collect(Collectors.toList());
    }

    //예약 신청내역 조회(partner)
    @Transactional
    public List<ReserveResponseDto> getReserveList(Long partnerId) {
        List<ReserveEntity> reserveEntities = reserveRepository.findAllByPartnerId(partnerId);
        return reserveEntities.stream()
                .map(ReserveResponseDto::from)
                .collect(Collectors.toList());
    }
    //예약신청 승인/거부(partner)
    @Transactional
    public ReserveResponseDto updateAcceptedStatus(Long partnerId,
                                       Long reserveId,
                                       Accepted accepted) {
        Long waitingNum = -1L;
        ReserveEntity reserveEntity = reserveRepository.findById(reserveId)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.NOT_FOUND_RESERVATION_HISTORY)
                );

        if (!reserveEntity.getPartnerId().equals(partnerId)) {
            throw new CustomException(ErrorCode.NOT_AUTH_UPDATE_RESERVATION);
        }
        //mysql 데이터 수정
        reserveEntity.setAccepted(accepted);
        waitingNum = generateWaitingNum();
        reserveEntity.setWaitingNumber(waitingNum);
        //redis 데이터 수정
        Waiting waiting =
                redisClient.get(reserveEntity.getStoreId(), Waiting.class);
        Optional<Waiting.Customer> waitingCustomer = waiting.getCustomerList().stream()
                .filter(e -> e.getReserveId().equals(reserveId))
                .findFirst();
        waitingCustomer.ifPresent(e -> e.setAccepted(accepted));
        Long finalWaitingNum = waitingNum;
        waitingCustomer.ifPresent(e -> e.setId(finalWaitingNum));
        redisClient.put(reserveEntity.getStoreId(), waiting);
        ReserveEntity updatedReserve = reserveRepository.save(reserveEntity);

        return ReserveResponseDto.from(updatedReserve);
    }
    private Long generateWaitingNum() {
        return redisTemplate.opsForValue().increment(ID_KEY, 1);
    }

    private void validateCustomer(ReserveRequestForm reserveRequestForm, Waiting waiting) {
        //가게 존재여부 확인
        StoreEntity storeEntity =
                storeRepository.findById(reserveRequestForm.getStoreId())
                        .orElseThrow(
                                () -> new CustomException(ErrorCode.NOT_FOUND_STORE)
                        );

        //가게 예약 가능상태 확인
        if (!storeEntity.isReservePossible()) {
            throw new CustomException(ErrorCode.RESERVE_NOT_AVAILABLE);
        }

        //회원 존재여부 확인
        if (!customerRepository.existsById(reserveRequestForm.getCustomerId())) {
            throw new CustomException(ErrorCode.NOT_FOUND_USER);
        }

        //이미 예약한 이력이 있으며 예약 유효시간이 지나지 않았다면 예약 거부
        boolean booked = waiting.getCustomerList().stream().filter(
                        customer ->
                                customer.getExpireTime()
                                        .isAfter(LocalDateTime.now()) ||
                                        customer.getAccepted()
                                                .equals(Accepted.WAITING)
                )
                .noneMatch(
                        customer ->
                                customer.getCustomerId().equals(
                                        reserveRequestForm.getCustomerId())
                );

        if (!booked) {
            throw new CustomException(ErrorCode.ALREADY_BOOKED_CUSTOMER);
        }
    }

    private Waiting.Customer validateWaitingCustomer(
            Waiting waiting, String customerName, String customerPhone
    ) {
        //예약리스트 존재 여부 확인
        if (waiting == null || waiting.getCustomerList().isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND_RESERVATION_HISTORY);
        }

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
            throw new CustomException(ErrorCode.EXPIRED_RESERVATION);
        }
        //accpeted 확인
        if (!waitingCustomer.getAccepted().equals(Accepted.ACCEPTED)) {
            throw new CustomException(ErrorCode.DENIED_RESERVATION);
        }

        waitingCustomer.setConfirm(true);
        return waitingCustomer;
    }
}
