package com.zerobase.reserve.service.search;

import com.zerobase.domain.dto.StoreDto;
import com.zerobase.domain.entity.StoreEntity;
import com.zerobase.domain.exception.CustomException;
import com.zerobase.domain.exception.ErrorCode;
import com.zerobase.domain.repository.StoreRepository;
import com.zerobase.reserve.service.CustomerSearchService;
import org.apache.commons.collections4.Trie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CustomerSearchServiceTest {

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private Trie<String, Void> trie;

    @InjectMocks
    private CustomerSearchService customerSearchService;

    private StoreEntity storeEntity;
    private StoreEntity.StoreDetailEntity storeDetailEntity;
    private StoreDto storeDto;
    private StoreDto.StoreDetailDto storeDetailDto;

    @BeforeEach
    void setUp() {
        storeDetailEntity = StoreEntity.StoreDetailEntity.builder()
                .id(1L)
                .address("address")
                .build();

        storeEntity = StoreEntity.builder()
                .id(1L)
                .name("Test Store")
                .storeDetail(storeDetailEntity)
                .build();

        storeDto = StoreDto.from(storeEntity);
    }

    @Test
    void searchStore_Success() {
        // given
        given(storeRepository.findByNameContaining(anyString())).willReturn(List.of(storeEntity));

        // when
        List<StoreDto> result = customerSearchService.searchStore("Test");

        // then
        assertEquals(1, result.size());
        assertEquals("Test Store", result.get(0).getName());
    }

    @Test
    void searchStore_NoMatch() {
        // given
        given(storeRepository.findByNameContaining(anyString())).willReturn(Collections.emptyList());

        // when
        CustomException exception = assertThrows(CustomException.class, () ->
                customerSearchService.searchStore("Test"));

        // then
        assertEquals(ErrorCode.NO_STORE_MATCH, exception.getErrorCode());
    }

    @Test
    void addAutoCompleteKeyword_InvalidInput() {
        // when
        CustomException exception = assertThrows(CustomException.class, () ->
                customerSearchService.addAutoCompleteKeyword(""));

        // then
        assertEquals(ErrorCode.INVALID_INPUT, exception.getErrorCode());
    }

    @Test
    void getAutoCompleteKeywords_Success() {
        // given
        SortedMap<String, Void> map = new TreeMap<>();
        map.put("Test", null);
        given(trie.prefixMap(anyString())).willReturn(map);

        // when
        List<String> result = customerSearchService.getAutoCompleteKeywords("Test");

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test", result.get(0));
    }

    @Test
    void deleteAutoCompleteKeywords_Success() {
        // given
        String storeName = "Test Store";

        // when
        customerSearchService.deleteAutoCompleteKeywords(storeName);

        // then
        // no exception thrown
    }

    @Test
    void getStoreDetail_Success() {
        // given
        given(storeRepository.findById(anyLong())).willReturn(Optional.of(storeEntity));

        // when
        StoreDto result = customerSearchService.getStoreDetail(1L);

        // then
        assertNotNull(result);
        assertEquals("address", result.getStoreDetailDto().getAddress());
    }

    @Test
    void getStoreDetail_NotFound() {
        // given
        given(storeRepository.findById(anyLong())).willReturn(Optional.empty());

        // when
        CustomException exception = assertThrows(CustomException.class, () ->
                customerSearchService.getStoreDetail(1L));

        // then
        assertEquals(ErrorCode.NOT_FOUND_STORE, exception.getErrorCode());
    }
}