package com.zerobase.reserve.service;

import com.zerobase.domain.dto.StoreDto;
import com.zerobase.domain.entity.StoreEntity;
import com.zerobase.domain.exception.CustomException;
import com.zerobase.domain.exception.ErrorCode;
import com.zerobase.domain.repository.StoreRepository;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.Trie;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerSearchService {

    private final StoreRepository storeRepository;
    private final Trie trie;

    public List<StoreDto> searchStore(String keyWord) {
        isNothingToSearch(keyWord);
        List<StoreEntity> storeList = storeRepository.findByNameContaining(keyWord);

        if (storeList.isEmpty()) {
            throw new CustomException(ErrorCode.NO_STORE_MATCH);
        }
        return storeList.stream()
                .map(StoreDto::from)
                .collect(Collectors.toList());
    }

    //auto complete
    public void addAutoCompleteKeyword(String keyWord) {
        isNothingToSearch(keyWord);
        trie.put(keyWord, null);
    }

    public List<String> getAutoCompleteKeywords(String keyWord) {
        isNothingToSearch(keyWord);
        return (List<String>) trie.prefixMap(keyWord)
                .keySet()
                .stream()
                .limit(10)
                .collect(Collectors.toList());
    }

    public void deleteAutoCompleteKeywords(String storeName) {
        isNothingToSearch(storeName);
        this.trie.remove(storeName);
    }


    public StoreDto getStoreDetail(Long storeId) {
        StoreEntity storeEntity = storeRepository.findById(storeId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_STORE)
        );

        StoreDto dto = StoreDto.from(storeEntity);
        return dto;
    }

    private void isNothingToSearch(String keyWord) {
        if (StringUtils.isBlank(keyWord)) {
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }
    }
}
