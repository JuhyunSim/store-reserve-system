package com.zerobase.partner.controller;

import com.zerobase.domain.requestForm.WithDrawalForm;
import com.zerobase.domain.security.config.JwtAuthProvider;
import com.zerobase.partner.service.WithDrawalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/withdrawal")
public class WithDrawalController {

    private final WithDrawalService withDrawalService;
    private final JwtAuthProvider jwtAuthProvider;

    @DeleteMapping("/partner")
    @PreAuthorize("hasRole('ROLE_PARTNER')")
    public ResponseEntity<?> partnerDelete(
            @RequestHeader(name = "Authorization") String token,
            @RequestBody WithDrawalForm withDrawalForm
    ) throws InvalidAlgorithmParameterException,
            NoSuchPaddingException,
            IllegalBlockSizeException,
            NoSuchAlgorithmException,
            BadPaddingException,
            InvalidKeyException {
        return ResponseEntity.ok(
                withDrawalService.deletePartner(
                        jwtAuthProvider.getId(token), withDrawalForm
                )
        );
    }

    @DeleteMapping("/customer")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public ResponseEntity<?> customerDelete(
            @RequestHeader(name = "Authorization") String token,
            @RequestBody WithDrawalForm withDrawalForm
    ) throws InvalidAlgorithmParameterException,
            NoSuchPaddingException,
            IllegalBlockSizeException,
            NoSuchAlgorithmException,
            BadPaddingException,
            InvalidKeyException {
        return ResponseEntity.ok(
                withDrawalService.deleteCustomer(
                        jwtAuthProvider.getId(token), withDrawalForm
                )
        );
    }
}
