package com.zerobase.partner.controller;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.zerobase.domain.requestForm.SignUpForm;
import com.zerobase.partner.application.SignUpApplication;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/signup")
@RequiredArgsConstructor
public class SignUpController {

    private final SignUpApplication signUpApplication;

    @PostMapping("/partner")
    public ResponseEntity<?> partnerSignUp(@RequestBody SignUpForm signUpForm)
            throws UnirestException {
        return ResponseEntity.ok(signUpApplication.partnerSignUp(signUpForm));
    }

    @PutMapping("/partner/verify")
    public ResponseEntity<?> partnerVerify(@RequestParam String email,
                                           @RequestParam String code
    ) {
        return ResponseEntity.ok(signUpApplication.partnerVerifySignUp(email));
    }

    @PostMapping("/customer")
    public ResponseEntity<?> customerSignUp(@RequestBody SignUpForm signUpForm)
            throws UnirestException {
        return ResponseEntity.ok(signUpApplication.customerSignUp(signUpForm));
    }

    @PutMapping("/customer/verify")
    public ResponseEntity<?> customerVerify(@RequestParam String email,
                                           @RequestParam String code
    ) {
        return ResponseEntity.ok(signUpApplication.customerVerifySignUp(email));
    }
}
