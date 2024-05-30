package com.zerobase.partner.controller;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.zerobase.partner.domain.SignUpForm;
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
    public ResponseEntity<?> signUp(@RequestBody SignUpForm signUpForm)
            throws UnirestException {
        return ResponseEntity.ok(signUpApplication.signUp(signUpForm));
    }

    @PutMapping("/partner/verify/")
    public ResponseEntity<?> verify(@RequestParam String email,
                                    @RequestParam String code
    ) {
        return ResponseEntity.ok(signUpApplication.verifySignUp(email));
    }
}
