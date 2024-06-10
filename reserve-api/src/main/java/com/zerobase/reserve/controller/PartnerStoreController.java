package com.zerobase.reserve.controller;

import com.zerobase.domain.dto.StoreDto;
import com.zerobase.domain.repository.StoreRepository;
import com.zerobase.domain.requestForm.store.StoreForm;
import com.zerobase.domain.requestForm.store.UpdateStoreForm;
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
    private final StoreRepository storeRepository;

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

    @GetMapping("/store/info")
    @PreAuthorize("hasRole('ROLE_PARTNER')")
    //나의 매장 조회하기
    public ResponseEntity<List<StoreDto>> getStoreInfo(
            @RequestHeader(name = "Authorization") String token
    ) throws InvalidAlgorithmParameterException,
            NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        return ResponseEntity.ok(storeService.getStoreInfo(
                jwtAuthProvider.getUserVo(token).getId()));
    }

    @PutMapping("/store/update")
    @PreAuthorize("hasRole('ROLE_PARTNER')")
    public ResponseEntity<?> updateStore(
            @RequestHeader(name = "Authorization") String token,
            @RequestBody UpdateStoreForm updateStoreForm
    ) throws InvalidAlgorithmParameterException, NoSuchPaddingException,
            IllegalBlockSizeException, NoSuchAlgorithmException,
            BadPaddingException, InvalidKeyException {
        StoreDto storeDto =
                storeService.updateStore(
                        jwtAuthProvider.getUserVo(token).getId(), updateStoreForm
                );
        return ResponseEntity.ok(storeDto);
    }

    @DeleteMapping("/store/delete")
    @PreAuthorize("hasRole('ROLE_PARTNER')")
    //나의 매장 조회하기
    public ResponseEntity<?> deleteStore(
            @RequestHeader(name = "Authorization") String token,
            @RequestParam List<Long> storeIds
    ) throws InvalidAlgorithmParameterException,
            NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {

        return ResponseEntity.ok(storeService.deleteStore(
                jwtAuthProvider.getUserVo(token).getId(), storeIds));
    }
}
