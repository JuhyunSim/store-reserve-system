package com.zerobase.reserve.controller;

import com.zerobase.domain.dto.StoreDto;
import com.zerobase.reserve.service.CustomerSearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class SearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerSearchService customerSearchService;

    private StoreDto storeDto;

    @BeforeEach
    void setUp() {
        StoreDto.StoreDetailDto storeDetailDto= StoreDto.StoreDetailDto.builder()
                .id(1L)
                .address("address")
                .build();

        storeDto = StoreDto.builder()
                .id(1L)
                .name("Test Store")
                .storeDetailDto(storeDetailDto)
                .build();
    }

    @Test
    void searchStore_Success() throws Exception {
        // given
        given(customerSearchService.searchStore(anyString()))
                .willReturn(List.of(storeDto));

        // when, then
        mockMvc.perform(get("/search")
                        .param("keyWord", "Test")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\":1,\"name\":\"Test Store\"}]"))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Test Store"));
    }

    @Test
    void getAutoComplete_Success() throws Exception {
        // given
        given(customerSearchService.getAutoCompleteKeywords(anyString()))
                .willReturn(List.of("Test"));

        // when, then
        mockMvc.perform(get("/search/search/autocomplete")
                        .param("keyWord", "Test")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("Test"));

    }

    @Test
    void getStoreDetails_Success() throws Exception {
        // given
        given(customerSearchService.getStoreDetail(anyLong()))
                .willReturn(storeDto);

        // when, then
        mockMvc.perform(get("/search/details")
                        .param("storeId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1,\"name\":\"Test Store\"}"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Store"))
                .andExpect(jsonPath("$.storeDetailDto.address").value("address"));
    }
}
