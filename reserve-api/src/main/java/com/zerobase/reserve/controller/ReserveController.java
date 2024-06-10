package com.zerobase.reserve.controller;

import com.zerobase.domain.constant.Accepted;
import com.zerobase.domain.dto.ReserveResponseDto;
import com.zerobase.domain.requestForm.ReserveRequestForm;
import com.zerobase.domain.security.config.JwtAuthProvider;
import com.zerobase.reserve.service.ReserveService;
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
@RequestMapping("/reserve")
@RequiredArgsConstructor
@Slf4j
public class ReserveController {

    private final ReserveService reserveService;
    private final JwtAuthProvider jwtAuthProvider;

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
        log.info("예약 신청이 완료되었습니다. 예약 승인 여부를 확인해주세요.");
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

    @GetMapping("/list")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public ResponseEntity<?> getCustomerReservations(
            @RequestHeader(name = "Authorization") String token
    ) throws InvalidAlgorithmParameterException, NoSuchPaddingException,
            IllegalBlockSizeException, NoSuchAlgorithmException,
            BadPaddingException, InvalidKeyException {
        Long customerId = jwtAuthProvider.getUserVo(token).getId();
        List<ReserveResponseDto> reservations =
                reserveService.getCustomerReservations(customerId);
        return ResponseEntity.ok(reservations);
    }

    //partner
    @GetMapping("/show")
    @PreAuthorize("hasRole('ROLE_PARTNER')")
    public ResponseEntity<?> getReserve(
            @RequestHeader(name = "Authorization") String token
    ) throws InvalidAlgorithmParameterException, NoSuchPaddingException,
            IllegalBlockSizeException, NoSuchAlgorithmException,
            BadPaddingException, InvalidKeyException {

        List<ReserveResponseDto> reserveList =
                reserveService.getReserveList(jwtAuthProvider.getUserVo(token)
                        .getId());

        return ResponseEntity.ok(reserveList);
    }

    @PutMapping("/accept")
    @PreAuthorize("hasRole('ROLE_PARTNER')")
    public ResponseEntity<?> acceptReserve(
            @RequestHeader(name = "Authorization") String token,
            @RequestParam Long reserveId,
            @RequestParam Accepted accepted
            ) throws InvalidAlgorithmParameterException, NoSuchPaddingException,
            IllegalBlockSizeException, NoSuchAlgorithmException,
            BadPaddingException, InvalidKeyException {
        Long partnerId = jwtAuthProvider.getUserVo(token).getId();
        return ResponseEntity.ok(reserveService
                .updateAcceptedStatus(partnerId, reserveId, accepted));
    }
}
