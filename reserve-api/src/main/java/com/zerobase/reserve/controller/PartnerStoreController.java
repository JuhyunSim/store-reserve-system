package com.zerobase.reserve.controller;

import com.zerobase.domain.requestForm.StoreForm;
import com.zerobase.domain.dto.StoreDto;
import com.zerobase.partner.security.config.JwtAuthProvider;
import com.zerobase.reserve.service.StoreService;
import com.zerobase.reserve.service.CustomerSearchService;
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

    private final StoreService storeService;
    private final CustomerSearchService customerSearchService;
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
//
//        log.info("Extracted email from token: {}", jwtAuthProvider.getEmail(token));
//        log.info("Retrieved store info: {}",
//                storeService.getStoreInfo(jwtAuthProvider.getEmail(token)));
//

        return ResponseEntity.ok(storeService.getStoreInfo(
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

        StoreDto storeDto = storeService.addStore(
                jwtAuthProvider.getId(token), storeForm
        );

        customerSearchService.addAutoCompleteKeyword(storeForm.getStoreName());

        return ResponseEntity.ok(storeDto);
    }
}
