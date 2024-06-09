package com.zerobase.reserve.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.domain.dto.ReviewDto;
import com.zerobase.reserve.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ReviewControllerReadTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewService reviewService;

    @Autowired
    private ObjectMapper objectMapper;

    private ReviewDto reviewDto1;
    private ReviewDto reviewDto2;

    @BeforeEach
    void setUp() {
        reviewDto1 = ReviewDto.builder()
                .id(1L)
                .content("Great service!")
                .rating(5)
                .storeId(1L)
                .customerId(1L)
                .title("Great service!")
                .build();

        reviewDto2 = ReviewDto.builder()
                .id(2L)
                .content("Good food!")
                .rating(4)
                .storeId(1L)
                .customerId(2L)
                .title("Good food!")
                .build();
    }

    @Test
    void getReview() throws Exception {
        given(reviewService.getReview(anyLong())).willReturn(reviewDto1);

        //when
        //then
        mockMvc.perform(get("/review/read")
                        .param("reviewId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(reviewDto1)));
    }

    @Test
    void getReviewList() throws Exception {
        given(reviewService.getReviewList(anyLong()))
                .willReturn(Arrays.asList(reviewDto1, reviewDto2));

        //when
        //then
        mockMvc.perform(get("/review/read/list")
                        .param("storeId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper
                        .writeValueAsString(Arrays.asList(reviewDto1, reviewDto2)
                        )
                ));
    }
}
