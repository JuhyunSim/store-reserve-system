package com.zerobase.partner.application;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.zerobase.domain.dto.PartnerDto;
import com.zerobase.partner.service.SignUpService;
import com.zerobase.partner.service.mailgun.MailgunApi;
import com.zerobase.domain.requestForm.SignUpForm;
import com.zerobase.domain.entity.PartnerEntity;
import com.zerobase.partner.service.mailgun.SendingMailForm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SignUpApplicationTest {

    @Mock
    private SignUpService signUpService;

    @Mock
    private MailgunApi mailgunApi;

    @InjectMocks
    private SignUpApplication signUpApplication;

    @Captor
    private ArgumentCaptor<SendingMailForm> sendingMailFormCaptor;

    @Test
    void successPartnerSignUpTest() throws UnirestException {
        //given
        SignUpForm signUpForm = SignUpForm.builder()
                .email("test@example.com")
                .password("password")
                .phone("010-1234-5678")
                .registerNumber("1234567890")
                .name("Test User")
                .build();

        PartnerEntity partnerEntity = PartnerEntity.builder()
                .email("test@example.com")
                .password("password")
                .phone("010-1234-5678")
                .registerNumber("1234567890")
                .name("Test User")
                .verificationCode("verificationCode")
                .verifyExpiredAt(LocalDateTime.now().plusDays(1))
                .build();

        given(signUpService.partnerIsValidEmail(any())).willReturn(false);
        given(signUpService.savePartnerEntity(any())).willReturn(PartnerDto.from(partnerEntity));

        //when
        PartnerDto partnerDto = signUpApplication.partnerSignUp(signUpForm);

        //then
        verify(mailgunApi, times(1)).sendVerifyEmail(sendingMailFormCaptor.capture());
        SendingMailForm sentMail = sendingMailFormCaptor.getValue();
        assertEquals("test@example.com", sentMail.getTo());
        assertEquals("Complete Signup with Verification", sentMail.getSubject());

        assertEquals("test@example.com", partnerDto.getEmail());
        assertEquals("password", partnerDto.getPassword());
        assertEquals("010-1234-5678", partnerDto.getPhone());
        assertEquals("1234567890", partnerDto.getRegisterNumber());
        assertEquals("Test User", partnerDto.getName());
        assertEquals("verificationCode",
                partnerDto.getVerificationCode());
        assertFalse(partnerDto.isVerify());
    }

    @Test
    void failPartnerSignUpTest() throws UnirestException {
        //given
        SignUpForm signUpForm = SignUpForm.builder()
                .email("test@example.com")
                .password("password")
                .phone("010-1234-5678")
                .registerNumber("1234567890")
                .name("Test User")
                .build();

        given(signUpService.partnerIsValidEmail(any())).willReturn(true);

        //when
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> signUpApplication.partnerSignUp(signUpForm));

        //then
        assertEquals("이미 등록된 회원입니다.", exception.getMessage());
    }


    @Test
    void successVerifyTest() {
        //given
        String email = "test@example.com";

        PartnerEntity partnerEntity = PartnerEntity.builder()
                .email("test@example.com")
                .password("password")
                .phone("010-1234-5678")
                .registerNumber("1234567890")
                .name("Test User")
                .verificationCode("verificationCode")
                .verifyExpiredAt(LocalDateTime.now().plusDays(1))
                .build();

        PartnerEntity updatedPartnerEntity = PartnerEntity.builder()
                .email("test@example.com")
                .password("password")
                .phone("010-1234-5678")
                .registerNumber("1234567890")
                .name("Test User")
                .verificationCode("verificationCode")
                .verifyExpiredAt(LocalDateTime.now().plusDays(1))
                .verify(true)
                .build();

        given(signUpService.findPartnerByEmail(anyString())).willReturn(partnerEntity);
        given(signUpService.savePartnerEntity(any())).willReturn(PartnerDto.from(updatedPartnerEntity));

        //when
        PartnerDto partnerDto = signUpApplication.partnerVerifySignUp(email);

        //then
        assertEquals("test@example.com", partnerDto.getEmail());
        assertEquals("******", partnerDto.getPassword());
        assertEquals("010-1234-5678", partnerDto.getPhone());
        assertEquals("1234567890", partnerDto.getRegisterNumber());
        assertEquals("Test User", partnerDto.getName());
        assertEquals("verificationCode",
                partnerDto.getVerificationCode());
        assertTrue(partnerDto.isVerify());
    }

    @Test
    void failVerifyPartnerSignUp() throws UnirestException {
        //given
        String email = "test@example.com";

        PartnerEntity partnerEntity = PartnerEntity.builder()
                .email("test@example.com")
                .password("password")
                .phone("010-1234-5678")
                .registerNumber("1234567890")
                .name("Test User")
                .verificationCode("verificationCode")
                .verifyExpiredAt(LocalDateTime.now().minusMinutes(1))
                .build();

        given(signUpService.findPartnerByEmail(anyString())).willReturn(partnerEntity);

        //when
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> signUpApplication.partnerVerifySignUp(email));

        //then
        assertEquals("인증기간이 만료되었습니다.", exception.getMessage());
    }

}