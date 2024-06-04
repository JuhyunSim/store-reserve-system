package com.zerobase.partner.service.customer;

import com.zerobase.partner.domain.dto.StoreDto;
import com.zerobase.partner.domain.model.StoreEntity;
import com.zerobase.partner.domain.repository.StoreRepository;
import org.apache.commons.collections4.Trie;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CustomerSearchServiceTest {

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private Trie trie;

    @InjectMocks
    private CustomerSearchService customerSearchService;

    @Test
    void searchStoreSuccess() {
        //given
        StoreEntity store1 = StoreEntity.builder()
                .name("aaabbb")
                .build();
        StoreEntity store2 = StoreEntity.builder()
                .name("a")
                .build();

        given(storeRepository.findByNameContaining(anyString()))
                .willReturn(List.of(store1, store2));

        //when
        List<StoreDto> storeDtoList = customerSearchService.searchStore("a");

        //then
        assertEquals("a", storeDtoList.get(1).getName());
        assertEquals("aaabbb", storeDtoList.get(0).getName());
    }

    @Test
    void searchStoreNotFound() {
        //given
        StoreEntity store1 = StoreEntity.builder()
                .name("aaabbb")
                .build();
        StoreEntity store2 = StoreEntity.builder()
                .name("a")
                .build();
        given(storeRepository.findByNameContaining(anyString()))
                .willReturn(List.of());

        //when
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> customerSearchService.searchStore("c"));

        //then
        assertEquals("검색어와 일치하는 매장이 없습니다.",
                exception.getMessage());


    }

    @Test
    void autoCompleteTest() {
        //given
        String keyword = "ap";
        SortedMap<String, String> words = new TreeMap<>();
        words.put("apple", null);
        words.put("apartment", null);
        words.put("approach", null);
        words.put("april", null);

        given(trie.prefixMap(anyString())).willReturn(words);

        //when
        List<String> result = customerSearchService.getAutoCompleteKeywords(keyword);

        //then
        assertEquals(4, result.size());
        assertEquals("apartment", result.get(0));
        assertEquals("apple", result.get(1));
        assertEquals("approach", result.get(2));
        assertEquals("april", result.get(3));
    }

}