package com.zerobase.reserve.service;

import com.zerobase.domain.dto.ReviewDto;
import com.zerobase.domain.entity.ReviewEntity;
import com.zerobase.domain.exception.CustomException;
import com.zerobase.domain.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceReadTest {
    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewService reviewService;

    private ReviewEntity reviewEntity1;
    private ReviewEntity reviewEntity2;

    @BeforeEach
    void setUp() {
        reviewEntity1 = ReviewEntity.builder()
                .id(1L)
                .storeId(1L)
                .customerId(1L)
                .content("Great service!")
                .rating(5)
                .build();

        reviewEntity2 = ReviewEntity.builder()
                .id(2L)
                .storeId(1L)
                .customerId(2L)
                .content("Good food!")
                .rating(4)
                .build();
    }

    @Test
    void getReviewTest() {
        // given
        given(reviewRepository.findById(anyLong())).willReturn(Optional.of(reviewEntity1));

        // when
        ReviewDto reviewDto = reviewService.getReview(1L);

        // then
        assertEquals(1L, reviewDto.getId());
        assertEquals("Great service!", reviewDto.getContent());
        assertEquals(5, reviewDto.getRating());
    }

    @Test
    void getReviewTest_ReviewNotFound() {
        // given
        given(reviewRepository.findById(anyLong())).willReturn(Optional.empty());

        // when
        // then
        assertThrows(CustomException.class, () -> reviewService.getReview(1L));
    }

    @Test
    void getReviewListTest() {
        // given
        given(reviewRepository.findAllByStoreId(anyLong()))
                .willReturn(Arrays.asList(reviewEntity1, reviewEntity2));

        // when
        List<ReviewDto> reviewList = reviewService.getReviewList(1L);

        // then
        assertEquals(2, reviewList.size());
        assertEquals(1L, reviewList.get(0).getId());
        assertEquals("Great service!", reviewList.get(0).getContent());
        assertEquals(5, reviewList.get(0).getRating());
        assertEquals(2L, reviewList.get(1).getId());
        assertEquals("Good food!", reviewList.get(1).getContent());
        assertEquals(4, reviewList.get(1).getRating());
    }
}
