package com.zerobase.reserve.service;

import com.zerobase.domain.dto.ReviewDto;
import com.zerobase.domain.entity.ReviewEntity;
import com.zerobase.domain.entity.ReserveEntity;
import com.zerobase.domain.exception.CustomException;
import com.zerobase.domain.exception.ErrorCode;
import com.zerobase.domain.repository.ReviewRepository;
import com.zerobase.domain.repository.ReserveRepository;
import com.zerobase.domain.requestForm.review.ReviewForm;
import com.zerobase.domain.requestForm.UpdateReviewForm;
import com.zerobase.domain.security.common.UserType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceAddAndUpdateTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ReserveRepository reserveRepository;

    @InjectMocks
    private ReviewService reviewService;

    private ReviewForm reviewForm;
    private UpdateReviewForm updateReviewForm;
    private ReviewEntity reviewEntity;
    private ReserveEntity reserveEntity;
    private UserType userType;
    private Long userId;

    @BeforeEach
    void setUp() {
        userType = UserType.CUSTOMER;
        userId = 1L;
        reviewForm = ReviewForm.builder()
                .content("Test content")
                .rating(4)
                .storeId(1L)
                .customerId(1L)
                .build();

        updateReviewForm = UpdateReviewForm.builder()
                .reviewId(1L)
                .content("Updated content")
                .rating(5)
                .reviewerId(1L)
                .build();

        reviewEntity = ReviewEntity.builder()
                .id(1L)
                .content("Test content")
                .rating(4)
                .storeId(1L)
                .customerId(1L)
                .build();

        reserveEntity = ReserveEntity.builder()
                .id(1L)
                .storeId(1L)
                .customerId(1L)
                .confirm(true)
                .build();
    }

    @Test
    void addReviewSuccessTest() {
        given(reserveRepository.findAllByCustomerId(anyLong())).willReturn(List.of(reserveEntity));
        given(reviewRepository.save(any(ReviewEntity.class))).willReturn(reviewEntity);

        ReviewDto result = reviewService.addReview(1L, reviewForm);

        assertNotNull(result);
        assertEquals("Test content", result.getContent());
        assertEquals(4, result.getRating());
    }

    @Test
    void addReviewFailureTest() {
        given(reserveRepository.findAllByCustomerId(anyLong())).willReturn(List.of());

        assertThrows(CustomException.class, () -> reviewService.addReview(1L, reviewForm));
    }

    @Test
    void updateReviewTest() {
        given(reviewRepository.findById(any(Long.class)))
                .willReturn(Optional.of(reviewEntity));
        given(reviewRepository.save(any(ReviewEntity.class)))
                .willReturn(reviewEntity);

        ReviewDto updatedReview = reviewService.updateReview(
                userType, userId, updateReviewForm);

        assertEquals("Updated content", updatedReview.getContent());
        assertEquals(5, updatedReview.getRating());
        assertEquals("Updated content", updatedReview.getTitle());
    }

    @Test
    void updateReviewUnauthorizedTest() {
        userType = UserType.PARTNER; // Changing userType to Partner

        CustomException exception = assertThrows(CustomException.class, () ->
                reviewService.updateReview(userType, userId, updateReviewForm));

        assertEquals(ErrorCode.NOT_AUTH_UPDATE_REVIEW, exception.getErrorCode());
    }

    @Test
    void updateReviewNotFoundTest() {
        given(reviewRepository.findById(any(Long.class)))
                .willReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () ->
                reviewService.updateReview(userType, userId, updateReviewForm));

        assertEquals(ErrorCode.NOT_FOUND_REVIEW, exception.getErrorCode());
    }
}
