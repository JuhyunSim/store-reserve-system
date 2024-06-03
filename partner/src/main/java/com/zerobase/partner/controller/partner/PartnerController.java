package com.zerobase.partner.controller.partner;

import com.zerobase.partner.security.config.JwtAuthProvider;
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
@RequestMapping("/partner")
@RequiredArgsConstructor
public class PartnerController {

    private final PartnerService partnerService;
    private final JwtAuthProvider jwtAuthProvider;

    @GetMapping("/info")
    @PreAuthorize("hasRole('PARTNER')")
    public ResponseEntity<?> getPartnerInfo(
            @RequestHeader(name = "X-Auth-Token") String token
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
}
