package com.zerobase.reserve.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.domain.security.common.UserType;
import com.zerobase.domain.security.common.UserVo;
import com.zerobase.domain.security.config.JwtAuthProvider;
import com.zerobase.reserve.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith({SpringExtension.class, MockitoExtension.class})
public class ReviewControllerDeleteTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReviewService reviewService;

    @MockBean
    private JwtAuthProvider jwtAuthProvider;

    private String token;
    private Long userId;
    private Long reviewId;

    @BeforeEach
    void setUp() throws InvalidAlgorithmParameterException,
            NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException,
            InvalidKeyException {
        token = "Bearer test-token";
        userId = 1L;
        reviewId = 1L;
        UserVo userVo = new UserVo(userId, "test@test.com");

        given(jwtAuthProvider.getUserType(token)).willReturn(UserType.PARTNER);
        given(jwtAuthProvider.getUserVo(token)).willReturn(userVo);

        mockMvc = MockMvcBuilders.standaloneSetup(
                new ReviewController(reviewService, jwtAuthProvider)
        ).build();
    }

    @Test
    void deleteReview() throws Exception {
        mockMvc.perform(delete("/review/delete")
                        .header("Authorization", token)
                        .param("reviewId", String.valueOf(reviewId)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void deleteReviews() throws Exception {
        mockMvc.perform(delete("/review/delete/list")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[1, 2, 3]"))
                .andExpect(status().isOk())
                .andDo(print());
    }
}
