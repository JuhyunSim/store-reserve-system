package com.zerobase.reserve.controller;

import com.zerobase.domain.requestForm.ReviewForm;
import com.zerobase.domain.requestForm.UpdateReviewForm;
import com.zerobase.domain.security.config.JwtAuthProvider;
import com.zerobase.reserve.service.ReviewService;
import jakarta.validation.Valid;
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
@RequestMapping("/review")
@RequiredArgsConstructor
@Slf4j
public class ReviewController {
    private final ReviewService reviewService;
    private final JwtAuthProvider jwtAuthProvider;

    @PostMapping("/add")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public ResponseEntity<?> addReview(
            @RequestHeader(name = "Authorization") String token,
            @Valid @RequestBody ReviewForm reviewForm
    ) throws InvalidAlgorithmParameterException, NoSuchPaddingException,
            IllegalBlockSizeException, NoSuchAlgorithmException,
            BadPaddingException, InvalidKeyException {

        return ResponseEntity.ok(reviewService.addReview(
                jwtAuthProvider.getUserVo(token).getId(), reviewForm)
        );
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('ROLE_CUSTOMER') or hasRole('ROLE_PARTNER')")
    public ResponseEntity<?> updateReview(
            @RequestHeader(name = "Authorization") String token,
            @Valid @RequestBody UpdateReviewForm updateReviewForm
    ) throws InvalidAlgorithmParameterException, NoSuchPaddingException,
            IllegalBlockSizeException, NoSuchAlgorithmException,
            BadPaddingException, InvalidKeyException {
        return ResponseEntity.ok(reviewService.updateReview(
                jwtAuthProvider.getUserType(token),
                jwtAuthProvider.getUserVo(token).getId(), updateReviewForm));
    }

    @GetMapping("/read")
    public ResponseEntity<?> getReview(
            @RequestParam Long reviewId
    ) {
        return ResponseEntity.ok(reviewService.getReview(reviewId));
    }

    @GetMapping("/read/list")
    public ResponseEntity<?> getReviewList(
            @RequestParam Long storeId
    ) {
        return ResponseEntity.ok(reviewService.getReviewList(storeId));
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('ROLE_PARTNER') or hasRole('ROLE_CUSTOMER')")
    public ResponseEntity<?> deleteReview(
            @RequestHeader(name = "Authorization") String token,
            @RequestParam Long reviewId
    ) throws InvalidAlgorithmParameterException, NoSuchPaddingException,
            IllegalBlockSizeException, NoSuchAlgorithmException,
            BadPaddingException, InvalidKeyException {

            int deleteCount = reviewService.deleteReview(
                    jwtAuthProvider.getUserType(token),
                    jwtAuthProvider.getUserVo(token).getId(),
                    reviewId
            );
        return ResponseEntity.ok( deleteCount + "개의 리뷰를 삭제했습니다."
        );
    }

    @DeleteMapping("/delete/list")
    @PreAuthorize("hasRole('ROLE_PARTNER') or hasRole('ROLE_CUSTOMER')")
    public ResponseEntity<?> deleteReviews(
            @RequestHeader(name = "Authorization") String token,
            @RequestBody List<Long> reviewIds
    ) throws InvalidAlgorithmParameterException, NoSuchPaddingException,
            IllegalBlockSizeException, NoSuchAlgorithmException,
            BadPaddingException, InvalidKeyException {
        int deletedCount = reviewService.deleteReviews(
                jwtAuthProvider.getUserType(token),
                jwtAuthProvider.getUserVo(token).getId(),
                reviewIds
        );
        return ResponseEntity.ok(deletedCount + "개의 리뷰를 삭제했습니다.");
    }
}
