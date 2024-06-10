package com.zerobase.reserve.service.reserve;

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
import com.zerobase.reserve.service.ReserveService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReserveServiceCustomerTest {

    @Mock
    private RedisClient redisClient;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ReserveRepository reserveRepository;

    @Mock
    private ValueOperations<String, Object> valueOperations;


    @InjectMocks
    private ReserveService reserveService;

    private ReserveEntity reserveEntity;
    private ReserveRequestForm reserveRequestForm;
    private Waiting waiting;

    /**
     * customer -> 예약 조회
     */
    @Test
    void getCustomerReservations_Success() {
        // given
        reserveEntity = ReserveEntity.builder()
                .id(1L)
                .storeId(1L)
                .partnerId(1L)
                .storeName("Test Store")
                .customerId(1L)
                .customerName("Test Customer")
                .waitingNumber(1L)
                .customerPhone("010-1234-5678")
                .reserveTime(LocalDateTime.now())
                .confirm(false)
                .accepted(Accepted.WAITING)
                .build();

        given(reserveRepository.findAllByCustomerId(anyLong()))
                .willReturn(Arrays.asList(reserveEntity));

        // when
        List<ReserveResponseDto> result =
                reserveService.getCustomerReservations(1L);

        // then
        assertEquals(1, result.size());
        assertEquals(reserveEntity.getCustomerName(), result.get(0).getCustomerName());
        verify(reserveRepository, times(1)).findAllByCustomerId(anyLong());
    }

    @Test
    void getCustomerReservations_NotFound() {
        // given
        reserveEntity = ReserveEntity.builder()
                .id(1L)
                .storeId(1L)
                .partnerId(1L)
                .storeName("Test Store")
                .customerId(2L)
                .customerName("Test Customer")
                .waitingNumber(1L)
                .customerPhone("010-1234-5678")
                .reserveTime(LocalDateTime.now())
                .confirm(false)
                .accepted(Accepted.WAITING)
                .build();

        given(reserveRepository.findAllByCustomerId(anyLong()))
                .willReturn(Collections.emptyList());

        // when
        CustomException exception = assertThrows(CustomException.class,
                () -> reserveService.getCustomerReservations(1L));

        // then
        assertEquals(ErrorCode.NOT_FOUND_RESERVATION, exception.getErrorCode());
        verify(reserveRepository, times(1)).findAllByCustomerId(anyLong());
    }

    /**
     * customer -> 예약 신청(추가)
     */
    @Test
    void addWaiting_Success() {
        // given
        reserveRequestForm = ReserveRequestForm.builder()
                .storeId(1L)
                .partnerId(1L)
                .customerId(1L)
                .customerName("Test Customer")
                .customerPhone("010-1234-5678")
                .reserveTime(LocalDateTime.now())
                .build();

        reserveEntity = ReserveEntity.from(reserveRequestForm);

        waiting = Waiting.builder()
                .storeId(1L)
                .customerList(new ArrayList<>())
                .build();

        StoreEntity storeEntity = StoreEntity.builder()
                .reservePossible(true)
                .build();

        given(redisClient.get(anyLong(), any())).willReturn(waiting);
        given(storeRepository.findById(anyLong())).willReturn(Optional.of(storeEntity));
        given(customerRepository.existsById(anyLong())).willReturn(true);
        given(reserveRepository.save(any(ReserveEntity.class))).willReturn(reserveEntity);

        // when
        ReserveResponseDto result = reserveService.addWaiting(reserveRequestForm);

        // then
        assertEquals(reserveEntity.getCustomerName(), result.getCustomerName());
        assertEquals(reserveEntity.getCustomerPhone(), result.getCustomerPhone());
        assertEquals(-1L, result.getWaitingNumber());
        assertEquals(Accepted.WAITING, result.getAccepted());
        assertFalse(result.isConfirm());
        assertEquals(reserveRequestForm.getReserveTime(), result.getReserveTime());
        verify(redisClient, times(1)).put(anyLong(), any());
        verify(reserveRepository, times(1)).save(any(ReserveEntity.class));
    }

    @Test
    void addWaiting_StoreNotFound() {
        // given
        reserveRequestForm = ReserveRequestForm.builder()
                .storeId(1L)
                .partnerId(1L)
                .customerId(1L)
                .customerName("Test Customer")
                .customerPhone("010-1234-5678")
                .reserveTime(LocalDateTime.now())
                .build();

        reserveEntity = ReserveEntity.builder()
                .id(1L)
                .waitingNumber(1L)
                .confirm(false)
                .accepted(Accepted.WAITING)
                .build();

        waiting = Waiting.builder()
                .storeId(1L)
                .customerList(new ArrayList<>())
                .build();

        given(redisClient.get(anyLong(), any())).willReturn(waiting);
        given(storeRepository.findById(anyLong())).willReturn(Optional.empty());

        // when
        CustomException exception = assertThrows(CustomException.class,
                () -> reserveService.addWaiting(reserveRequestForm));

        // then
        assertEquals(ErrorCode.NOT_FOUND_STORE, exception.getErrorCode());
    }

    @Test
    void addWaiting_CustomerNotFound() {
        // given
        reserveRequestForm = ReserveRequestForm.builder()
                .storeId(1L)
                .partnerId(1L)
                .customerId(1L)
                .customerName("Test Customer")
                .customerPhone("010-1234-5678")
                .reserveTime(LocalDateTime.now())
                .build();

        waiting = Waiting.builder()
                .storeId(1L)
                .customerList(new ArrayList<>())
                .build();

        StoreEntity storeEntity = StoreEntity.builder()
                .reservePossible(true)
                .build();

        given(redisClient.get(anyLong(), any())).willReturn(waiting);
        given(storeRepository.findById(anyLong())).willReturn(Optional.of(storeEntity));
        given(customerRepository.existsById(anyLong())).willReturn(false);

        // when
        CustomException exception = assertThrows(CustomException.class,
                () -> reserveService.addWaiting(reserveRequestForm));

        // then
        assertEquals(ErrorCode.NOT_FOUND_USER, exception.getErrorCode());
    }

    @Test
    void addWaiting_AlreadyBookedCustomer() {
        // given
        reserveRequestForm = ReserveRequestForm.builder()
                .storeId(1L)
                .partnerId(1L)
                .customerId(1L)
                .customerName("Test Customer")
                .customerPhone("010-1234-5678")
                .reserveTime(LocalDateTime.now())
                .build();

        waiting = Waiting.builder()
                .storeId(1L)
                .customerList(new ArrayList<>())
                .build();

        waiting.getCustomerList().add(
                Waiting.Customer.builder()
                        .customerId(1L)
                        .name("Test Customer")
                        .phone("010-1234-5678")
                        .expireTime(LocalDateTime.now().plusHours(1))
                        .accepted(Accepted.WAITING)
                        .build()
        );

        StoreEntity storeEntity = StoreEntity.builder()
                .reservePossible(true)
                .build();

        given(redisClient.get(anyLong(), any())).willReturn(waiting);
        given(storeRepository.findById(anyLong())).willReturn(Optional.of(storeEntity));
        given(customerRepository.existsById(anyLong())).willReturn(true);

        // when
        CustomException exception = assertThrows(CustomException.class,
                () -> reserveService.addWaiting(reserveRequestForm));

        // then
        assertEquals(ErrorCode.ALREADY_BOOKED_CUSTOMER, exception.getErrorCode());
    }


    @Test
    void addWaiting_StoreNotAvailable() {
        // given
        reserveRequestForm = ReserveRequestForm.builder()
                .storeId(1L)
                .partnerId(1L)
                .customerId(1L)
                .customerName("Test Customer")
                .customerPhone("010-1234-5678")
                .reserveTime(LocalDateTime.now())
                .build();

        waiting = Waiting.builder()
                .storeId(1L)
                .customerList(new ArrayList<>())
                .build();

        waiting.getCustomerList().add(
                Waiting.Customer.builder()
                        .customerId(1L)
                        .name("Test Customer")
                        .phone("010-1234-5678")
                        .expireTime(LocalDateTime.now().plusHours(1))
                        .accepted(Accepted.WAITING)
                        .build()
        );

        StoreEntity storeEntity = StoreEntity.builder()
                .reservePossible(false)
                .build();

        given(redisClient.get(anyLong(), any())).willReturn(waiting);
        given(storeRepository.findById(anyLong())).willReturn(Optional.of(storeEntity));

        // when
        CustomException exception = assertThrows(CustomException.class,
                () -> reserveService.addWaiting(reserveRequestForm));

        // then
        assertEquals(ErrorCode.RESERVE_NOT_AVAILABLE, exception.getErrorCode());
    }

    /**
     * customer -> 예약 확인(수정)
     */
    @Test
    void confirmReserve_Success() {
        // given
        reserveEntity = ReserveEntity.builder()
                .id(1L)
                .storeId(1L)
                .partnerId(1L)
                .customerId(1L)
                .customerName("Test Customer")
                .customerPhone("010-1234-5678")
                .reserveTime(LocalDateTime.of(2024, 7, 1, 12, 0))
                .confirm(false)
                .accepted(Accepted.ACCEPTED)
                .build();

        reserveRequestForm = ReserveRequestForm.builder()
                .storeId(1L)
                .partnerId(1L)
                .customerId(1L)
                .customerName("Test Customer")
                .customerPhone("010-1234-5678")
                .reserveTime(LocalDateTime.of(2024, 7, 1, 12, 0))
                .build();

        Waiting.Customer waitingCustomer =
                Waiting.Customer.from(reserveRequestForm);
        waitingCustomer.setAccepted(Accepted.ACCEPTED);
        waiting = Waiting.builder()
                .storeId(1L)
                .customerList(new ArrayList<>())
                .build();
        waiting.getCustomerList().add(waitingCustomer);

        given(redisClient.get(anyLong(), any())).willReturn(waiting);
        given(reserveRepository.findAllByStoreId(anyLong())).willReturn(List.of(reserveEntity));
        given(reserveRepository.save(any(ReserveEntity.class))).willReturn(reserveEntity);

        // when
        ReserveResponseDto result = reserveService.confirmReserve(
                1L, "Test Customer", "010-1234-5678");

        // then
        assertEquals(true, result.isConfirm());
        assertEquals(Accepted.ACCEPTED, result.getAccepted());
        assertEquals("Test Customer", result.getCustomerName());
        verify(redisClient, times(1)).put(anyLong(), any());
        verify(reserveRepository, times(1)).save(any(ReserveEntity.class));
    }

    @Test
    void confirmReserve_WaitingListNotFound() {
        given(redisClient.get(anyLong(), eq(Waiting.class))).willReturn(null);

        CustomException exception = assertThrows(CustomException.class,
                () -> reserveService.confirmReserve(
                        1L,
                        "Test Customer",
                        "010-1234-5678"));

        assertEquals(ErrorCode.NOT_FOUND_RESERVATION_HISTORY, exception.getErrorCode());
    }

    @Test
    void confirmReserve_ReservationrNotFound() {
        //given
        waiting = Waiting.builder()
                .storeId(1L)
                .customerList(new ArrayList<>())
                .build();

        given(redisClient.get(anyLong(), eq(Waiting.class))).willReturn(null);

        //when
        //then
        CustomException exception = assertThrows(CustomException.class,
                () -> reserveService.confirmReserve(1L, "Test Customer", "010-1234-5678"));

        assertEquals(ErrorCode.NOT_FOUND_RESERVATION_HISTORY, exception.getErrorCode());
    }

    @Test
    void confirmReserve_ExpiredReservation() {
        Waiting.Customer waitingCustomer = Waiting.Customer.builder()
                .customerId(1L)
                .name("Test Customer")
                .phone("010-1234-5678")
                .reserveTime(LocalDateTime.now().minusMinutes(20))
                .expireTime(LocalDateTime.now().minusMinutes(10))
                .accepted(Accepted.WAITING)
                .build();
        waiting = Waiting.builder()
                .storeId(1L)
                .customerList(new ArrayList<>())
                .build();
        waiting.getCustomerList().add(waitingCustomer);

        given(redisClient.get(anyLong(), eq(Waiting.class))).willReturn(waiting);

        CustomException exception = assertThrows(CustomException.class,
                () -> reserveService.confirmReserve(1L, "Test Customer", "010-1234-5678"));

        assertEquals(ErrorCode.EXPIRED_RESERVATION, exception.getErrorCode());
    }

    @Test
    void confirmReserve_DeniedReservation() {
        Waiting.Customer waitingCustomer = Waiting.Customer.builder()
                .customerId(1L)
                .name("Test Customer")
                .phone("010-1234-5678")
                .reserveTime(LocalDateTime.now().plusMinutes(20))
                .expireTime(LocalDateTime.now().plusMinutes(10))
                .accepted(Accepted.DENIED)
                .build();
        waiting = Waiting.builder()
                .storeId(1L)
                .customerList(new ArrayList<>())
                .build();
        waiting.getCustomerList().add(waitingCustomer);

        given(redisClient.get(anyLong(), eq(Waiting.class))).willReturn(waiting);

        CustomException customException = assertThrows(CustomException.class, () ->
                reserveService.confirmReserve(1L,
                        "Test Customer",
                        "010-1234-5678"));

        assertEquals(ErrorCode.DENIED_RESERVATION, customException.getErrorCode());
    }

    @Test
    void confirmReserve_NotFoundEqualReservation() {
        //given
        Waiting.Customer waitingCustomer = Waiting.Customer.builder()
                .customerId(1L)
                .name("Test Customer")
                .phone("010-1234-5678")
                .reserveTime(LocalDateTime.now().plusMinutes(20))
                .expireTime(LocalDateTime.now().plusMinutes(10))
                .accepted(Accepted.DENIED)
                .build();
        waiting = Waiting.builder()
                .storeId(1L)
                .customerList(new ArrayList<>())
                .build();
        waiting.getCustomerList().add(waitingCustomer);

        given(redisClient.get(anyLong(), eq(Waiting.class))).willReturn(waiting);

        //when
        CustomException customException = assertThrows(CustomException.class, () -> reserveService.confirmReserve(1L,
                "Test Customer2",
                "010-1234-5678")
        );

        assertEquals(ErrorCode.NOT_FOUND_RESERVATION, customException.getErrorCode());
    }
}