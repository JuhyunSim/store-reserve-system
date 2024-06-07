package com.zerobase.partner.application;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.zerobase.domain.requestForm.SignUpForm;
import com.zerobase.domain.dto.CustomerDto;
import com.zerobase.domain.dto.PartnerDto;
import com.zerobase.domain.entity.CustomerEntity;
import com.zerobase.domain.entity.PartnerEntity;
import com.zerobase.partner.service.SignUpService;
import com.zerobase.partner.service.mailgun.MailgunApi;
import com.zerobase.partner.service.mailgun.SendingMailForm;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SignUpApplication {

    private final SignUpService signUpService;
    private final MailgunApi mailgunApi;

    @Transactional
    public PartnerDto partnerSignUp(SignUpForm signUpForm) throws UnirestException {
        //사용 가능한 이메일 여부 확인
        if (signUpService.partnerIsValidEmail(signUpForm)) {
            throw new RuntimeException("이미 등록된 회원입니다.");
        }

        //인증 이메일 발송
        String code = generateRandomCode();
        String email = signUpForm.getEmail();
        String name = signUpForm.getName();
        String text = getVerifyEmail(name, email, code, "partner");
        SendingMailForm sendingMailForm = SendingMailForm.builder()
                .from("verify@store.com")
                .to(email)
                .subject("Complete Signup with Verification")
                .text(text)
                .build();

        mailgunApi.sendVerifyEmail(sendingMailForm);

        //partner 데이터 저장
        PartnerEntity partnerEntity = PartnerEntity.from(signUpForm);
        partnerEntity.setVerificationCode(code);
        partnerEntity.setVerifyExpiredAt(LocalDateTime.now().plusDays(1));
        return signUpService.savePartnerEntity(partnerEntity);
    }

    @Transactional
    public PartnerDto partnerVerifySignUp(String email) {
        PartnerEntity partnerEntity = signUpService.findPartnerByEmail(email);

        if (partnerEntity.getVerifyExpiredAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("인증기간이 만료되었습니다.");
        }

        partnerEntity.setVerify(true);
        return signUpService.savePartnerEntity(partnerEntity);
    }

    @Transactional
    public CustomerDto customerSignUp(SignUpForm signUpForm)
            throws UnirestException {
        //사용 가능한 이메일 여부 확인
        if (signUpService.customerIsValidEmail(signUpForm)) {
            throw new RuntimeException("이미 등록된 회원입니다.");
        }

        //인증 이메일 발송
        String code = generateRandomCode();
        String email = signUpForm.getEmail();
        String name = signUpForm.getName();
        String text = getVerifyEmail(name, email, code, "customer");
        SendingMailForm sendingMailForm = SendingMailForm.builder()
                .from("verify@store.com")
                .to(email)
                .subject("Complete Signup with Verification")
                .text(text)
                .build();

        mailgunApi.sendVerifyEmail(sendingMailForm);

        //customer 데이터 저장
        CustomerEntity customerEntity = CustomerEntity.from(signUpForm);
        customerEntity.setVerificationCode(code);
        customerEntity.setVerifyExpiredAt(LocalDateTime.now().plusDays(1));
        return signUpService.saveCustomerEntity(customerEntity);
    }

    @Transactional
    public CustomerDto customerVerifySignUp(String email) {
        CustomerEntity customerEntity = signUpService.findCustomerByEmail(email);

        if (customerEntity.getVerifyExpiredAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("인증기간이 만료되었습니다.");
        }

        customerEntity.setVerify(true);
        return signUpService.saveCustomerEntity(customerEntity);
    }

    private String generateRandomCode() {
        return RandomStringUtils.random(10, true, true);
    }

    private String getVerifyEmail(
            String name,
            String email,
            String code,
            String userType) {
        StringBuilder sb = new StringBuilder();
        return sb.append("Hello, ").append(name).append("!\n")
                .append("Please click the link below " +
                        "if you want to complete SingUp\n")
                .append("http://localhost:8080/signup/" +
                        userType +
                        "/verify/email=")
                .append(email)
                .append("&code=")
                .append(code).toString();
    }
}
