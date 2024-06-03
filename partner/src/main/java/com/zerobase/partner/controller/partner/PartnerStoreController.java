package com.zerobase.partner.controller.partner;

import com.zerobase.partner.domain.StoreForm;
import com.zerobase.partner.domain.dto.StoreDto;
import com.zerobase.partner.security.config.JwtAuthProvider;
import com.zerobase.partner.service.PartnerStoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@RestController
@RequestMapping("/partner")
@RequiredArgsConstructor
@Slf4j
public class PartnerStoreController {

    private final PartnerStoreService partnerStoreService;
    private final JwtAuthProvider jwtAuthProvider;

    @GetMapping("/store/info")
    @PreAuthorize("hasRole('ROLE_PARTNER')")
    //나의 매장 조회하기
    public ResponseEntity<List<StoreDto>> getStoreInfo(
            @RequestHeader(name = "Authorization") String token)
            throws InvalidAlgorithmParameterException,
            NoSuchPaddingException,
            IllegalBlockSizeException,
            NoSuchAlgorithmException,
            BadPaddingException,
            InvalidKeyException {

        log.info("Extracted email from token: {}", jwtAuthProvider.getEmail(token));
        log.info("Retrieved store info: {}",
                partnerStoreService.getStoreInfo(jwtAuthProvider.getEmail(token)));


        return ResponseEntity.ok(partnerStoreService.getStoreInfo(
                jwtAuthProvider.getEmail(token))
        );
    }

    @PostMapping("/store/add")
    @PreAuthorize("hasRole('ROLE_PARTNER')")
    public ResponseEntity<?> addStore(
            @RequestHeader(name = "Authorization") String token,
            @RequestBody StoreForm storeForm
    ) throws InvalidAlgorithmParameterException,
            NoSuchPaddingException,
            IllegalBlockSizeException,
            NoSuchAlgorithmException,
            BadPaddingException,
            InvalidKeyException {

        // 토큰 유효성 검사
        if (!jwtAuthProvider.validateToken(token)) {
            return ResponseEntity.status(401).body("Invalid token");
        }

        StoreDto storeDto = partnerStoreService.addStore(
                jwtAuthProvider.getId(token), storeForm
        );

        return ResponseEntity.ok(storeDto);
    }
}
