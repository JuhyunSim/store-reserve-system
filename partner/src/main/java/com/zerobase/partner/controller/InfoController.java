package com.zerobase.partner.controller;

import com.zerobase.partner.security.config.JwtAuthProvider;
import com.zerobase.partner.service.CustomerService;
import com.zerobase.partner.service.PartnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/info")
@RequiredArgsConstructor
public class InfoController {

    private final PartnerService partnerService;
    private final CustomerService customerService;
    private final JwtAuthProvider jwtAuthProvider;

    @GetMapping("/partner")
    @PreAuthorize("hasRole('ROLE_PARTNER')")
    public ResponseEntity<?> getPartnerInfo(
            @RequestHeader(name = "Authorization") String token
    ) throws InvalidAlgorithmParameterException,
            NoSuchPaddingException,
            IllegalBlockSizeException,
            NoSuchAlgorithmException,
            BadPaddingException,
            InvalidKeyException {

        return ResponseEntity.ok(partnerService.getPartnerInfo(
                jwtAuthProvider.getId(token),
                jwtAuthProvider.getEmail(token)
                )
        );
    }

    @GetMapping("/customer")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public ResponseEntity<?> getCustomerInfo(
            @RequestHeader(name = "Authorization") String token
    ) throws InvalidAlgorithmParameterException,
            NoSuchPaddingException,
            IllegalBlockSizeException,
            NoSuchAlgorithmException,
            BadPaddingException,
            InvalidKeyException {

        return ResponseEntity.ok(customerService.getCustomerInfo(
                        jwtAuthProvider.getId(token),
                        jwtAuthProvider.getEmail(token)
                )
        );
    }
}
