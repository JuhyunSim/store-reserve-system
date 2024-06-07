package com.zerobase.reserve.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.partner.security.common.UserType;
import com.zerobase.partner.security.config.JwtAuthProvider;
import com.zerobase.partner.security.encrypt.Aes256Utils;
import com.zerobase.domain.requestForm.StoreForm;
import com.zerobase.domain.dto.StoreDto;
import com.zerobase.domain.entity.StoreEntity;
import com.zerobase.reserve.service.StoreService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PartnerStoreControllerTest {

    @Mock
    private StoreService storeService;

    @Mock
    private JwtAuthProvider jwtAuthProvider;

    @Mock
    private Aes256Utils aes256Utils;

    @InjectMocks
    private PartnerStoreController partnerStoreController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void addStore_ValidToken_StoreAddedSuccessfully() throws Exception {
        // Given
        Claims claims = Jwts.claims()
                .add("roles", UserType.PARTNER)
                .subject(aes256Utils.encrypt("1111@gmail.com"))
                .id(aes256Utils.encrypt("1L")).build();

        String token = Jwts.builder()
                .claims(claims)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30))
                .compact();

        StoreForm storeForm = new StoreForm();
        storeForm.setStoreName("Test Store");
        storeForm.setLatitude(1.11);
        storeForm.setLongitude(1.11);

        StoreDto expectedStoreDto = StoreDto.from(StoreEntity.builder()
                        .name(storeForm.getStoreName())
                        .latitude(storeForm.getLatitude())
                        .longitude(storeForm.getLongitude())
                        .build());

        given(storeService.addStore(anyLong(), any(StoreForm.class)))
                .willReturn(expectedStoreDto);
        given(jwtAuthProvider.validateToken(anyString())).willReturn(true);

        // When
        ResponseEntity<?> responseEntity = partnerStoreController.addStore(token, storeForm);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        verify(storeService, times(1))
                .addStore(anyLong(), any(StoreForm.class));
    }


}