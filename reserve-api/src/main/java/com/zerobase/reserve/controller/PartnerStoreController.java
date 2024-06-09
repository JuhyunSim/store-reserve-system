package com.zerobase.reserve.controller;

import com.zerobase.domain.dto.StoreDto;
import com.zerobase.domain.requestForm.StoreForm;
import com.zerobase.domain.security.config.JwtAuthProvider;
import com.zerobase.reserve.service.CustomerSearchService;
import com.zerobase.reserve.service.StoreService;
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
    public ResponseEntity<List<StoreDto>> getStoreInfo(@RequestParam Long id)
            throws InvalidAlgorithmParameterException,
            NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        return ResponseEntity.ok(storeService.getStoreInfo(id));
    }

    @PostMapping("/store/add")
    @PreAuthorize("hasRole('ROLE_PARTNER')")
    public ResponseEntity<?> addStore(
            @RequestBody StoreForm storeForm
    ) throws InvalidAlgorithmParameterException, NoSuchPaddingException,
            IllegalBlockSizeException, NoSuchAlgorithmException,
            BadPaddingException, InvalidKeyException {
        StoreDto storeDto =
                storeService.addStore(storeForm);
        customerSearchService.addAutoCompleteKeyword(storeForm.getStoreName());
        return ResponseEntity.ok(storeDto);
    }
}
