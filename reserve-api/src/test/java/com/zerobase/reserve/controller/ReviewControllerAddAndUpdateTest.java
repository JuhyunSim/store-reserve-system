package com.zerobase.reserve.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.domain.dto.ReviewDto;
import com.zerobase.domain.requestForm.review.ReviewForm;
import com.zerobase.domain.requestForm.UpdateReviewForm;
import com.zerobase.domain.security.common.UserType;
import com.zerobase.domain.security.common.UserVo;
import com.zerobase.domain.security.config.JwtAuthProvider;
import com.zerobase.reserve.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@AutoConfigureMockMvc
public class ReviewControllerAddAndUpdateTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewService reviewService;

    @MockBean
    private JwtAuthProvider jwtAuthProvider;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;
    private Long userId;
    private ReviewForm reviewForm;
    private UpdateReviewForm updateReviewForm;
    private ReviewDto reviewDto;

    @BeforeEach
    void setUp() {
        token = "Bearer test-token";
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
                .partnerId(2L)  // Assuming partnerId for validation
                .build();

        reviewDto = ReviewDto.builder()
                .id(1L)
                .content("Test content")
                .rating(4)
                .storeId(1L)
                .customerId(1L)
                .title("Test title")
                .build();
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void addReviewTest() throws Exception {
        given(jwtAuthProvider.getUserVo(token)).willReturn(new UserVo(userId, "test@example.com"));
        given(reviewService.addReview(any(Long.class), any(ReviewForm.class))).willReturn(reviewDto);

        mockMvc.perform(post("/review/add")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewForm)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(reviewDto)));
    }

    @Test
    @WithMockUser(roles = {"CUSTOMER", "PARTNER"})
    void updateReviewTest() throws Exception {
        given(jwtAuthProvider.getUserVo(token)).willReturn(new UserVo(userId, "test@example.com"));
        given(jwtAuthProvider.getUserType(token)).willReturn(UserType.CUSTOMER);
        given(reviewService.updateReview(any(UserType.class), any(Long.class), any(UpdateReviewForm.class))).willReturn(reviewDto);

        mockMvc.perform(put("/review/update")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReviewForm)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(reviewDto)));
    }
}
