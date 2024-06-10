package com.zerobase.reserve.controller.reserve;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.domain.constant.Accepted;
import com.zerobase.domain.dto.ReserveResponseDto;
import com.zerobase.domain.entity.ReserveEntity;
import com.zerobase.domain.requestForm.ReserveRequestForm;
import com.zerobase.domain.security.common.UserVo;
import com.zerobase.domain.security.config.JwtAuthProvider;
import com.zerobase.reserve.service.ReserveService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class ReserveControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReserveService reserveService;

    @MockBean
    private JwtAuthProvider jwtAuthProvider;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;
    private ReserveRequestForm reserveRequestForm;
    private ReserveEntity reserveEntity;
    private ReserveResponseDto reserveResponseDto;

    @BeforeEach
    void setUp() throws InvalidAlgorithmParameterException,
            NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        token = "Bearer test-token";
        reserveRequestForm = ReserveRequestForm.builder()
                .storeId(1L)
                .customerId(1L)
                .partnerId(1L)
                .customerName("customerName")
                .customerPhone("customerPhone")
                .reserveTime(LocalDateTime.now())
                .build();

        UserVo userVo = new UserVo(1L, "test@example.com");

        given(jwtAuthProvider.getUserVo(token)).willReturn(userVo);
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void testReserveStore() throws Exception {
        //given
        reserveEntity = ReserveEntity.from(reserveRequestForm);
        reserveEntity.setId(1L);
        reserveResponseDto = ReserveResponseDto.from(reserveEntity);

        given(reserveService.addWaiting(any(ReserveRequestForm.class))).willReturn(reserveResponseDto);

        mockMvc.perform(post("/reserve/add")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reserveRequestForm)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(reserveResponseDto)))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.customerId").value(1L))
                .andExpect(jsonPath("$.customerName").value("customerName"))
                .andExpect(jsonPath("$.customerPhone").value("customerPhone"))
                .andExpect(jsonPath("$.confirm").value(false))
                .andExpect(jsonPath("$.accepted").value(Accepted.WAITING.name()))
                .andExpect(jsonPath("$.reserveTime").isNotEmpty());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void testConfirmReserve() throws Exception {
        // given
        reserveEntity = ReserveEntity.builder()
                .id(1L)
                .customerId(1L)
                .customerName("customerName")
                .customerPhone("customerPhone")
                .reserveTime(LocalDateTime.now())
                .confirm(false)
                .accepted(Accepted.ACCEPTED)
                .build();

        reserveResponseDto = ReserveResponseDto.builder()
                .id(1L)
                .customerId(1L)
                .customerName("customerName")
                .customerPhone("customerPhone")
                .reserveTime(reserveEntity.getReserveTime())
                .confirm(true)
                .accepted(Accepted.ACCEPTED)
                .build();

        given(reserveService.confirmReserve(anyLong(), any(), any()))
                .willAnswer(invocation -> {
                    Long storeId = invocation.getArgument(0);
                    String name = invocation.getArgument(1);
                    String phone = invocation.getArgument(2);

                    reserveEntity.setConfirm(true);

                    return ReserveResponseDto.from(reserveEntity);
                });

        mockMvc.perform(put("/reserve/confirm")
                        .param("storeId", "1")
                        .param("name", "customerName")
                        .param("phone", "customerPhone"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(reserveResponseDto)))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.customerId").value(1L))
                .andExpect(jsonPath("$.customerName").value("customerName"))
                .andExpect(jsonPath("$.customerPhone").value("customerPhone"))
                .andExpect(jsonPath("$.confirm").value(true))
                .andExpect(jsonPath("$.accepted").value(Accepted.ACCEPTED.name()))
                .andExpect(jsonPath("$.reserveTime").isNotEmpty());;

        // then
        assertTrue(reserveEntity.isConfirm());
        verify(reserveService, times(1))
                .confirmReserve(anyLong(), any(), any());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void testGetCustomerReservations() throws Exception {
        //given
        reserveResponseDto = ReserveResponseDto.builder()
                .id(1L)
                .customerId(1L)
                .customerName("customerName")
                .customerPhone("customerPhone")
                .reserveTime(LocalDateTime.now())
                .confirm(true)
                .accepted(Accepted.ACCEPTED)
                .build();

        given(reserveService.getCustomerReservations(anyLong()))
                .willReturn(List.of(reserveResponseDto));

        //when
        //then
        mockMvc.perform(get("/reserve/list")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper
                        .writeValueAsString(Collections.singletonList(reserveResponseDto))))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].customerId").value(1L))
                .andExpect(jsonPath("$[0].customerName").value("customerName"))
                .andExpect(jsonPath("$[0].customerPhone").value("customerPhone"))
                .andExpect(jsonPath("$[0].confirm").value(true))
                .andExpect(jsonPath("$[0].accepted").value(Accepted.ACCEPTED.name()))
                .andExpect(jsonPath("$[0].reserveTime").isNotEmpty());;
    }

    @Test
    @WithMockUser(roles = "PARTNER")
    void testGetReserve() throws Exception {
        //given
        reserveResponseDto = ReserveResponseDto.builder()
                .id(1L)
                .customerId(1L)
                .customerName("customerName")
                .customerPhone("customerPhone")
                .reserveTime(LocalDateTime.now())
                .confirm(true)
                .accepted(Accepted.ACCEPTED)
                .build();

        given(reserveService.getReserveList(anyLong())).willReturn(
                List.of(reserveResponseDto));

        //when
        //then
        mockMvc.perform(get("/reserve/show")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Collections.singletonList(reserveResponseDto))))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].customerId").value(1L))
                .andExpect(jsonPath("$[0].customerName").value("customerName"))
                .andExpect(jsonPath("$[0].customerPhone").value("customerPhone"))
                .andExpect(jsonPath("$[0].confirm").value(true))
                .andExpect(jsonPath("$[0].accepted").value(Accepted.ACCEPTED.name()))
                .andExpect(jsonPath("$[0].reserveTime").isNotEmpty());

        verify(reserveService, times(1))
                .getReserveList(anyLong());
    }

    @Test
    @WithMockUser(roles = "PARTNER")
    void testAcceptReserve() throws Exception {
        reserveResponseDto = ReserveResponseDto.builder()
                .id(1L)
                .customerId(1L)
                .customerName("customerName")
                .customerPhone("customerPhone")
                .reserveTime(LocalDateTime.now())
                .confirm(true)
                .accepted(Accepted.ACCEPTED)
                .build();

        given(reserveService.updateAcceptedStatus(anyLong(), anyLong(), any(Accepted.class)))
                .willReturn(reserveResponseDto);

        mockMvc.perform(put("/reserve/accept")
                        .header("Authorization", token)
                        .param("reserveId", "1")
                        .param("accepted", "ACCEPTED"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(reserveResponseDto)));
    }
}