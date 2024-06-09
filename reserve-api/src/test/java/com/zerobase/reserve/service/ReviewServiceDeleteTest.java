package com.zerobase.reserve.service;

import com.zerobase.domain.entity.ReviewEntity;
import com.zerobase.domain.exception.CustomException;
import com.zerobase.domain.repository.ReviewRepository;
import com.zerobase.domain.security.common.UserType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceDeleteTest {
    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewService reviewService;

    @Test
    void deleteReviewAsPartnerSuccessTest() {
        //given
        Long reviewId = 1L;
        Long partnerId = 1L;
        ReviewEntity reviewEntity = ReviewEntity.builder().id(reviewId).partnerId(partnerId).build();
        given(reviewRepository.findById(reviewId)).willReturn(Optional.of(reviewEntity));
        doNothing().when(reviewRepository).deleteById(reviewId);

        //when
        int result = reviewService.deleteReview(UserType.PARTNER, partnerId, reviewId);

        //then
        assertEquals(1, result);
    }

    @Test
    void deleteReviewAsCustomerSuccessTest() {
        //given
        Long reviewId = 1L;
        Long customerId = 1L;
        ReviewEntity reviewEntity = ReviewEntity.builder().id(reviewId).customerId(customerId).build();
        given(reviewRepository.findById(reviewId)).willReturn(Optional.of(reviewEntity));
        doNothing().when(reviewRepository).deleteById(reviewId);

        //When
        int result = reviewService.deleteReview(UserType.CUSTOMER, customerId, reviewId);

        //then
        assertEquals(1, result);
    }

    @Test
    void deleteReviewNotFoundTest() {
        //given
        Long reviewId = 1L;
        given(reviewRepository.findById(reviewId)).willReturn(Optional.empty());

        //when
        //then
        CustomException customException = assertThrows(CustomException.class, () -> reviewService.deleteReview(UserType.PARTNER, 1L, reviewId));
        assertEquals("리뷰를 찾을 수 없습니다.", customException.getMessage());
    }

    @Test
    void deleteReviewsSuccessTest() {
        //given
        Long reviewId1 = 1L;
        Long reviewId2 = 2L;
        Long reviewId3 = 3L;
        Long partnerId = 1L;
        ReviewEntity reviewEntity1 = ReviewEntity.builder().id(reviewId1).partnerId(partnerId).build();
        ReviewEntity reviewEntity2 = ReviewEntity.builder().id(reviewId2).partnerId(partnerId).build();
        ReviewEntity reviewEntity3 = ReviewEntity.builder().id(reviewId3).partnerId(partnerId).build();
        given(reviewRepository.findById(reviewId1)).willReturn(Optional.of(reviewEntity1));
        given(reviewRepository.findById(reviewId2)).willReturn(Optional.of(reviewEntity2));
        given(reviewRepository.findById(reviewId3)).willReturn(Optional.of(reviewEntity3));
        doNothing().when(reviewRepository).deleteById(reviewId1);
        doNothing().when(reviewRepository).deleteById(reviewId2);
        doNothing().when(reviewRepository).deleteById(reviewId3);

        //when
        int result = reviewService.deleteReviews(UserType.PARTNER, partnerId, Arrays.asList(reviewId1, reviewId2, reviewId3));

        //then
        assertEquals(3, result);
    }

    @Test
    void deleteReviewsNotFoundTest() {
        //given
        Long reviewId1 = 1L;
        Long reviewId2 = 2L;
        Long reviewId3 = 3L;
        Long partnerId = 1L;
        ReviewEntity reviewEntity1 = ReviewEntity.builder().id(reviewId1).partnerId(partnerId).build();
        ReviewEntity reviewEntity2 = ReviewEntity.builder().id(reviewId2).partnerId(partnerId).build();
        given(reviewRepository.findById(reviewId1)).willReturn(Optional.of(reviewEntity1));
        given(reviewRepository.findById(reviewId2)).willReturn(Optional.of(reviewEntity2));
        given(reviewRepository.findById(reviewId3)).willReturn(Optional.empty());
        doNothing().when(reviewRepository).deleteById(reviewId1);

        //when
        //then
        CustomException customException = assertThrows(CustomException.class, () -> reviewService.deleteReviews(UserType.PARTNER, partnerId, Arrays.asList(reviewId1, reviewId2, reviewId3)));
        assertEquals("리뷰를 찾을 수 없습니다.", customException.getMessage());
    }
}
