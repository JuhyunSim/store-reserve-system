package com.zerobase.partner.controller.partner;

import com.zerobase.partner.application.SignInApplication;
import com.zerobase.partner.domain.SignInForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/signin")
@RequiredArgsConstructor
@Slf4j
public class SignInController {

    private final SignInApplication signinApplication;

    @PostMapping("/partner")
    public ResponseEntity<?> signIn(@RequestBody SignInForm signInForm)
            throws InvalidAlgorithmParameterException,
            NoSuchPaddingException,
            IllegalBlockSizeException,
            NoSuchAlgorithmException,
            BadPaddingException,
            InvalidKeyException {
        String token = signinApplication.signIn(signInForm);
        log.info("{} 님이 로그인하였습니다.", signInForm.getEmail());
        return ResponseEntity.ok(token);
    }
}
