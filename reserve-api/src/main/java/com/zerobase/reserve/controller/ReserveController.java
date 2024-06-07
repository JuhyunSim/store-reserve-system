package com.zerobase.reserve.controller;

import com.zerobase.domain.dto.ReserveDto;
import com.zerobase.domain.requestForm.ReserveForm;
import com.zerobase.partner.security.config.JwtAuthProvider;
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
    private final JwtAuthProvider jwtAuthProvider;

    @PostMapping("/add")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public ResponseEntity<?> reserveStore(
            @RequestHeader(name = "Authorization") String token,
            @RequestBody ReserveForm reserveForm
    ) throws InvalidAlgorithmParameterException,
            NoSuchPaddingException,
            IllegalBlockSizeException,
            NoSuchAlgorithmException,
            BadPaddingException,
            InvalidKeyException {
        ReserveDto reserveDto =
                reserveService.addWaiting(jwtAuthProvider.getId(token), reserveForm);

        return ResponseEntity.ok(reserveDto);
    }

    @PutMapping("/confirm")
    public ResponseEntity<?> confirmReserve(
            @RequestParam Long storeId,
            @RequestParam String name,
            @RequestParam String phone
    ) {
        ReserveDto reserveDto = reserveService.confirmReserve(storeId, name, phone);
        return ResponseEntity.ok(reserveDto);
    }
}
