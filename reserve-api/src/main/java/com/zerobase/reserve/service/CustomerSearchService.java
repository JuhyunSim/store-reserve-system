package com.zerobase.reserve.service;

import com.zerobase.domain.dto.StoreDto;
import com.zerobase.domain.entity.StoreEntity;
import com.zerobase.domain.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.Trie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerSearchService {

    private final StoreRepository storeRepository;
    private final Trie trie;

    @Transactional
    public List<StoreDto> searchStore(String keyWord) {
        List<StoreEntity> storeList = storeRepository.findByNameContaining(keyWord);

        if (storeList.isEmpty()) {
            throw new RuntimeException("검색어와 일치하는 매장이 없습니다.");
        }
        return storeList.stream()
                .map(StoreDto::from)
                .collect(Collectors.toList());
    }

    //auto complete
    @Transactional
    public void addAutoCompleteKeyword(String keyword) {
        trie.put(keyword, null);
    }

    @Transactional
    public List<String> getAutoCompleteKeywords(String keyword) {

        return (List<String>) trie.prefixMap(keyword)
                .keySet()
                .stream()
                .limit(10)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteAutoCompleteKeywords(String storeName) {
        this.trie.remove(storeName);
    }

}
