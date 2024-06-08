package com.zerobase.reserve.controller;

import com.zerobase.domain.dto.ReserveResponseDto;
import com.zerobase.domain.requestForm.ReserveRequestForm;
import com.zerobase.reserve.service.ReserveService;
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
@RequestMapping("/waiting")
@RequiredArgsConstructor
public class ReserveController {

    private final ReserveService reserveService;

    @PostMapping("/add")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public ResponseEntity<?> reserveStore(
            @RequestBody ReserveRequestForm reserveRequestForm
    ) throws InvalidAlgorithmParameterException,
            NoSuchPaddingException,
            IllegalBlockSizeException,
            NoSuchAlgorithmException,
            BadPaddingException,
            InvalidKeyException {
        ReserveResponseDto reserveResponseDto =
                reserveService.addWaiting(reserveRequestForm);

        return ResponseEntity.ok(reserveResponseDto);
    }

    @PutMapping("/confirm")
    public ResponseEntity<?> confirmReserve(
            @RequestParam Long storeId,
            @RequestParam String name,
            @RequestParam String phone
    ) {
        ReserveResponseDto reserveResponseDto = reserveService.confirmReserve(storeId, name, phone);
        return ResponseEntity.ok(reserveResponseDto);
    }
}
