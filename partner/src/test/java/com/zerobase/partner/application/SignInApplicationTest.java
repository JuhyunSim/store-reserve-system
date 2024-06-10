package com.zerobase.partner.application;

import com.zerobase.domain.entity.CustomerEntity;
import com.zerobase.domain.entity.PartnerEntity;
import com.zerobase.domain.exception.CustomException;
import com.zerobase.domain.exception.ErrorCode;
import com.zerobase.domain.requestForm.SignInForm;
import com.zerobase.domain.security.common.UserType;
import com.zerobase.domain.security.common.UserVo;
import com.zerobase.domain.security.config.JwtAuthProvider;
import com.zerobase.partner.service.SignInService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SignInApplicationTest {

    @Mock
    private SignInService signInService;

    @Mock
    private JwtAuthProvider jwtAuthProvider;

    @InjectMocks
    private SignInApplication signInApplication;

    @Test
    void testSignInPartner_Success() throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        // Given
        SignInForm signInForm = SignInForm.builder()
                .email("partner@example.com")
                .password("password")
                .build();
        PartnerEntity partnerEntity = PartnerEntity.builder()
                .email("partner@example.com")
                .id(1L)
                .build();
        UserVo userVo = new UserVo(1L, partnerEntity.getEmail());
        UserType userType = UserType.PARTNER;

        given(signInService.partnerFindEmailAndComparePassword(signInForm))
                .willReturn(partnerEntity);
        given(jwtAuthProvider.generateToken(
                partnerEntity.getEmail(), partnerEntity.getId(), UserType.PARTNER))
                .willReturn("mockToken");
        given(jwtAuthProvider.getUserType(anyString())).willReturn(userType);
        given(jwtAuthProvider.getUserVo(anyString())).willReturn(userVo);

        // When
        String token = signInApplication.signInPartner(signInForm);

        // Then
        assertNotNull(token);
        assertEquals("mockToken", token);
        assertEquals("partner@example.com", jwtAuthProvider.getUserVo(token).getEmail());
        assertEquals(1L, jwtAuthProvider.getUserVo(token).getId());
        assertEquals(UserType.PARTNER, jwtAuthProvider.getUserType(token));

    }

    @Test
    void testSignInCustomer_Success() throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        // Given
        SignInForm signInForm = SignInForm.builder()
                .email("partner@example.com")
                .password("password")
                .build();
        CustomerEntity customerEntity = CustomerEntity.builder()
                .email("customer@example.com")
                .id(1L)
                .build();
        UserType userType = UserType.CUSTOMER;
        UserVo userVo = new UserVo(1L, customerEntity.getEmail());
        given(signInService.customerFindEmailAndComparePassword(signInForm))
                .willReturn(customerEntity);
        given(jwtAuthProvider.generateToken(
                customerEntity.getEmail(), customerEntity.getId(), UserType.CUSTOMER))
                .willReturn("mockToken");
        given(jwtAuthProvider.getUserType(anyString())).willReturn(userType);

        given(jwtAuthProvider.getUserVo(anyString())).willReturn(userVo);

        // When
        String token = signInApplication.signInCustomer(signInForm);

        // Then
        assertNotNull(token);
        assertEquals("mockToken", token);
        assertEquals(UserType.CUSTOMER, jwtAuthProvider.getUserType(token));
        assertEquals(1L, jwtAuthProvider.getUserVo(token).getId());
        assertEquals("customer@example.com", jwtAuthProvider.getUserVo(token).getEmail());
    }

    @Test
    void testSignInPartner_InvalidCredentials() {
        // Given
        SignInForm signInForm = new SignInForm("partner@example.com", "wrongpassword");
        when(signInService.partnerFindEmailAndComparePassword(signInForm)).thenThrow(new RuntimeException("Invalid credentials"));

        // When
        Exception exception = assertThrows(RuntimeException.class, () -> {
            signInApplication.signInPartner(signInForm);
        });

        // Then
        assertEquals("Invalid credentials", exception.getMessage());
    }

    @Test
    void testSignInCustomer_WrongPassword() {
        // Given
        SignInForm signInForm = new SignInForm("customer@example.com", "wrongpassword");
        given(signInService.customerFindEmailAndComparePassword(signInForm)).willThrow(new CustomException(ErrorCode.CHECK_PASSWORD));

        // When
        CustomException exception = assertThrows(CustomException.class, () -> {
            signInApplication.signInCustomer(signInForm);
        });

        // Then
        assertEquals(ErrorCode.CHECK_PASSWORD, exception.getErrorCode());
    }
}
