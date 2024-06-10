package com.zerobase.reserve.service.reserve;

import com.zerobase.domain.constant.Accepted;
import com.zerobase.domain.dto.ReserveResponseDto;
import com.zerobase.domain.entity.ReserveEntity;
import com.zerobase.domain.exception.CustomException;
import com.zerobase.domain.exception.ErrorCode;
import com.zerobase.domain.redis.RedisClient;
import com.zerobase.domain.redis.Waiting;
import com.zerobase.domain.repository.ReserveRepository;
import com.zerobase.reserve.service.ReserveService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith (MockitoExtension.class)
public class ReserveServicePartnerTest {

    @Mock
    private ReserveRepository reserveRepository;

    @Mock
    private RedisClient redisClient;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @InjectMocks
    private ReserveService reserveService;

    private ReserveEntity reserveEntity;
    private Waiting waiting;

    @Test
    void getReserveList_Success() {
        // given
        reserveEntity = ReserveEntity.builder()
                .id(1L)
                .storeId(1L)
                .partnerId(1L)
                .customerName("Test Customer")
                .customerPhone("010-1234-5678")
                .reserveTime(LocalDateTime.now().plusMinutes(20))
                .confirm(false)
                .accepted(Accepted.WAITING)
                .build();

        List<ReserveEntity> reserveEntities = List.of(reserveEntity);
        when(reserveRepository.findAllByPartnerId(anyLong())).thenReturn(reserveEntities);

        // when
        List<ReserveResponseDto> result = reserveService.getReserveList(1L);

        // then
        assertEquals(1, result.size());
        assertEquals("Test Customer", result.get(0).getCustomerName());
        verify(reserveRepository, times(1)).findAllByPartnerId(anyLong());
    }


    @Test
    void testUpdateAcceptedStatus_Success() {
        //given
        reserveEntity = ReserveEntity.builder()
                .id(1L)
                .partnerId(1L)
                .storeId(1L)
                .build();

        waiting = new Waiting();
        Waiting.Customer customer = new Waiting.Customer();
        customer.setReserveId(1L);
        waiting.setCustomerList(Collections.singletonList(customer));

        given(reserveRepository.findById(anyLong())).willReturn(Optional.of(reserveEntity));
        given(reserveRepository.save(any(ReserveEntity.class))).willReturn(reserveEntity);
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.increment(anyString(), anyLong())).willReturn(1L);
        given(redisClient.get(1L, Waiting.class)).willReturn(waiting);

        //when
        ReserveResponseDto responseDto =
                reserveService.updateAcceptedStatus(
                        1L, 1L, Accepted.DENIED);

        //then
        assertNotNull(responseDto);
        assertEquals(Accepted.DENIED, responseDto.getAccepted());
        assertEquals(1L, responseDto.getWaitingNumber());

        verify(reserveRepository, times(1)).findById(1L);
        verify(reserveRepository, times(1)).save(any(ReserveEntity.class));
        verify(redisClient, times(1)).get(1L, Waiting.class);
        verify(redisClient, times(1)).put(anyLong(), any(Waiting.class));
    }

    @Test
    void testUpdateAcceptedStatus_ReservationNotFound() {
        //given
        given(reserveRepository.findById(1L)).willReturn(Optional.empty());

        //when
        //then
        CustomException exception = assertThrows(CustomException.class, () -> {
            reserveService.updateAcceptedStatus(1L, 1L, Accepted.ACCEPTED);
        });

        assertEquals(ErrorCode.NOT_FOUND_RESERVATION_HISTORY, exception.getErrorCode());
        verify(reserveRepository, times(1)).findById(1L);
        verify(reserveRepository, times(0)).save(any(ReserveEntity.class));
        verify(redisClient, times(0)).get(1L, Waiting.class);
        verify(redisClient, times(0)).put(anyLong(), any(Waiting.class));
    }

    @Test
    void testUpdateAcceptedStatus_UnauthorizedPartner() {
        reserveEntity = ReserveEntity.builder()
                .id(1L)
                .build();
        reserveEntity.setPartnerId(2L);

        given(reserveRepository.findById(1L)).willReturn(Optional.of(reserveEntity));

        CustomException exception = assertThrows(CustomException.class, () -> {
            reserveService.updateAcceptedStatus(1L, 1L, Accepted.ACCEPTED);
        });

        assertEquals(ErrorCode.NOT_AUTH_UPDATE_RESERVATION, exception.getErrorCode());

        verify(reserveRepository, times(1)).findById(1L);
        verify(reserveRepository, times(0)).save(any(ReserveEntity.class));
        verify(redisClient, times(0)).get(1L, Waiting.class);
        verify(redisClient, times(0)).put(anyLong(), any(Waiting.class));
    }
}
