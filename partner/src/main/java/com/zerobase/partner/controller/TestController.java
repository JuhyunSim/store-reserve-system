package com.zerobase.partner.controller;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.zerobase.partner.mailgun.MailgunApi;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class TestController {
    private final MailgunApi mailgunApi;

    @PostMapping("/test")
    public ResponseEntity<?> test() throws UnirestException {
        return ResponseEntity.ok(mailgunApi.sendVerifyEmail());
    }


}
