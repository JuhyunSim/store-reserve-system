package com.zerobase.reserve.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.domain.dto.StoreDto;
import com.zerobase.domain.requestForm.store.StoreForm;
import com.zerobase.domain.requestForm.store.UpdateStoreForm;
import com.zerobase.domain.security.common.UserVo;
import com.zerobase.domain.security.config.JwtAuthProvider;
import com.zerobase.reserve.service.CustomerSearchService;
import com.zerobase.reserve.service.StoreService;
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

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@AutoConfigureMockMvc
public class PartnerStoreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StoreService storeService;

    @MockBean
    private CustomerSearchService customerSearchService;

    @MockBean
    private JwtAuthProvider jwtAuthProvider;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;
    private Long partnerId;
    private StoreForm storeForm;
    private UpdateStoreForm updateStoreForm;
    private StoreDto storeDto;

    @BeforeEach
    void setUp() {
        token = "Bearer test-token";
        partnerId = 1L;
        UserVo userVo = new UserVo(partnerId, "test@test.com");
    }

    @Test
    @WithMockUser(roles = "PARTNER")
    void addStoreTest() throws Exception {

        storeForm = StoreForm.builder()
                .partnerId(partnerId)
                .storeName("Test Store")
                .description("Test Description")
                .latitude(0.0)
                .longitude(0.0)
                .build();

        storeDto = StoreDto.builder()
                .id(1L)
                .partnerId(partnerId)
                .name("Test Store")
                .description("Test Description")
                .latitude(0.0)
                .longitude(0.0)
                .reservePossible(true)
                .build();

        given(jwtAuthProvider.getUserVo(token)).willReturn(new UserVo(partnerId, "test@test.com"));
        given(storeService.addStore(any(StoreForm.class))).willReturn(storeDto);

        mockMvc.perform(post("/partner/store/add")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(storeForm)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(storeDto)))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Store"))
                .andExpect(jsonPath("$.description").value("Test Description"));;
    }

    @Test
    @WithMockUser(roles = "PARTNER")
    void getStoreInfoTest() throws Exception {
        storeDto = StoreDto.builder()
                .id(1L)
                .partnerId(partnerId)
                .name("Test Store")
                .description("Test Description")
                .latitude(0.0)
                .longitude(0.0)
                .reservePossible(true)
                .build();

        given(jwtAuthProvider.getUserVo(token)).willReturn(new UserVo(partnerId, "test@test.com"));
        given(storeService.getStoreInfo(partnerId)).willReturn(Collections.singletonList(storeDto));

        mockMvc.perform(get("/partner/store/info")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Collections.singletonList(storeDto))))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Test Store"))
                .andExpect(jsonPath("$[0].description").value("Test Description"))
                .andExpect(jsonPath("$[0].reservePossible").value(true));

    }

    @Test
    @WithMockUser(roles = "PARTNER")
    void updateStoreTest() throws Exception {
        updateStoreForm = UpdateStoreForm.builder()
                .storeId(1L)
                .partnerId(partnerId)
                .storeName("Updated Store")
                .description("Updated Description")
                .latitude(1.0)
                .longitude(1.0)
                .reservePossible(false)
                .build();

        storeDto = StoreDto.builder()
                .id(1L)
                .partnerId(partnerId)
                .name("Test Store")
                .description("Test Description")
                .latitude(0.0)
                .longitude(0.0)
                .reservePossible(true)
                .build();

        given(jwtAuthProvider.getUserVo(token)).willReturn(new UserVo(partnerId, "test@test.com"));
        given(storeService.updateStore(any(Long.class), any(UpdateStoreForm.class))).willReturn(storeDto);

        mockMvc.perform(put("/partner/store/update")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateStoreForm)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(storeDto)))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Store"))
                .andExpect(jsonPath("$.description").value("Test Description"));
    }

    @Test
    @WithMockUser(roles = "PARTNER")
    void deleteStoreTest() throws Exception {
        given(jwtAuthProvider.getUserVo(token)).willReturn(new UserVo(partnerId, "test@test.com"));
        given(storeService.deleteStore(any(Long.class), any(List.class))).willReturn(1);

        mockMvc.perform(delete("/partner/store/delete")
                        .header("Authorization", token)
                        .param("storeIds", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }
}