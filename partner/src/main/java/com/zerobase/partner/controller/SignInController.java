package com.zerobase.partner.controller;

import com.zerobase.partner.domain.SignInForm;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/signin")
public class SignInController {

    @PostMapping("/partner")
    public ResponseEntity<?> signIn(@RequestBody SignInForm signInForm) {
        return null;
    }
}
