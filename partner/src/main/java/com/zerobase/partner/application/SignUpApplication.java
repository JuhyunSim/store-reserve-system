package com.zerobase.partner.application;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.zerobase.partner.domain.SignUpForm;
import com.zerobase.partner.domain.dto.PartnerDto;
import com.zerobase.partner.domain.model.PartnerEntity;
import com.zerobase.partner.service.SignUpService;
import com.zerobase.partner.service.mailgun.MailgunApi;
import com.zerobase.partner.service.mailgun.SendingMailForm;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SignUpApplication {

    private final SignUpService signUpService;
    private final MailgunApi mailgunApi;

    public PartnerDto signUp(SignUpForm signUpForm) throws UnirestException {
        //사용 가능한 이메일 여부 확인
        if (signUpService.isValidEmail(signUpForm)) {
            throw new RuntimeException("이미 등록된 회원입니다.");
        }

        //인증 이메일 발송
        String code = generateRandomCode();
        String email = signUpForm.getEmail();
        String name = signUpForm.getName();
        String text = getVerifyEmail(name, email, code);
        SendingMailForm sendingMailForm = SendingMailForm.builder()
                .from("verify@store.com")
                .to(email)
                .subject("Complete Signup with Verification")
                .text(text)
                .build();

        mailgunApi.sendVerifyEmail(sendingMailForm);

        //partner 데이터 저장
        PartnerEntity partnerEntity = signUpForm.from();
        partnerEntity.setVerificationCode(code);
        partnerEntity.setVerifyExpiredAt(LocalDateTime.now().plusDays(1));
        return signUpService.savePartnerEntity(partnerEntity);
    }

    public PartnerDto verifySignUp(String email) {
        PartnerEntity partnerEntity = signUpService.findPartnerByEmail(email);

        if (partnerEntity.getVerifyExpiredAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("인증기간이 만료되었습니다.");
        }

        partnerEntity.setVerify(true);
        return signUpService.savePartnerEntity(partnerEntity);
    }

    private String generateRandomCode() {
        return RandomStringUtils.random(10, true, true);
    }

    private String getVerifyEmail(String name, String email, String code) {
        StringBuilder sb = new StringBuilder();
        return sb.append("Hello, ").append(name).append("!\n")
                .append("Please click the link below " +
                        "if you want to complete SingUp\n")
                .append("http://localhost:8080/signup/partner/verify/email=")
                .append(email)
                .append("&code=")
                .append(code).toString();
    }
}
