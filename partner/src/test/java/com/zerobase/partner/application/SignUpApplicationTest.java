package com.zerobase.partner.application;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.zerobase.partner.domain.SignUpForm;
import com.zerobase.partner.domain.dto.PartnerDto;
import com.zerobase.partner.domain.model.PartnerEntity;
import com.zerobase.partner.service.PartnerService;
import com.zerobase.partner.service.mailgun.MailgunApi;
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
    private PartnerService partnerService;

    @Mock
    private MailgunApi mailgunApi;

    @InjectMocks
    private SignUpApplication signUpApplication;

    @Captor
    private ArgumentCaptor<SendingMailForm> sendingMailFormCaptor;

    @Test
    void successSignUpTest() throws UnirestException {
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

        given(partnerService.isValidEmail(any())).willReturn(false);
        given(partnerService.savePartnerEntity(any())).willReturn(partnerEntity.from());

        //when
        PartnerDto partnerDto = signUpApplication.signUp(signUpForm);

        //then
        verify(mailgunApi, times(1)).sendVerifyEmail(sendingMailFormCaptor.capture());
        SendingMailForm sentMail = sendingMailFormCaptor.getValue();
        assertEquals("test@example.com", sentMail.getTo());
        assertEquals("Complete Signup with Verification", sentMail.getSubject());

        assertEquals("test@example.com", partnerDto.getEmail());
        assertEquals("******", partnerDto.getPassword());
        assertEquals("010-1234-5678", partnerDto.getPhone());
        assertEquals("1234567890", partnerDto.getRegisterNumber());
        assertEquals("Test User", partnerDto.getName());
        assertEquals("verificationCode",
                partnerDto.getVerificationCode());
        assertFalse(partnerDto.isVerify());
    }

    @Test
    void failSignUpTest() throws UnirestException {
        //given
        SignUpForm signUpForm = SignUpForm.builder()
                .email("test@example.com")
                .password("password")
                .phone("010-1234-5678")
                .registerNumber("1234567890")
                .name("Test User")
                .build();

        given(partnerService.isValidEmail(any())).willReturn(true);

        //when
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> signUpApplication.signUp(signUpForm));

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

        given(partnerService.findPartnerByEmail(anyString())).willReturn(partnerEntity);
        given(partnerService.savePartnerEntity(any())).willReturn(updatedPartnerEntity.from());

        //when
        PartnerDto partnerDto = signUpApplication.verifySignUp(email);

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
    void failVerifySignUp() throws UnirestException {
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

        given(partnerService.findPartnerByEmail(anyString())).willReturn(partnerEntity);

        //when
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> signUpApplication.verifySignUp(email));

        //then
        assertEquals("인증기간이 만료되었습니다.", exception.getMessage());
    }

}