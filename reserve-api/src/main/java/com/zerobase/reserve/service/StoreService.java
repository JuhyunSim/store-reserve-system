package com.zerobase.reserve.service;

import com.zerobase.domain.dto.StoreDto;
import com.zerobase.domain.entity.PartnerEntity;
import com.zerobase.domain.entity.StoreEntity;
import com.zerobase.domain.repository.PartnerRepository;
import com.zerobase.domain.repository.StoreRepository;
import com.zerobase.domain.requestForm.StoreForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.Trie;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoreService {


    private final StoreRepository storeRepository;

    private final PartnerRepository partnerRepository;
    private final Trie trie;

    //등록
    public StoreDto addStore(Long partnerId, StoreForm storeForm) {
        //partnerId와 storeName을 기준으로 이미 있으면 추가 제외
        boolean exist = storeRepository.existsByPartnerIdAndName(
                        partnerId, storeForm.getStoreName());
        if (exist) {
            throw new RuntimeException("Store Already Exists");
        }

        return StoreDto.from(
                storeRepository.save(StoreEntity.of(partnerId, storeForm))
        );
    }

    //조회
    public List<StoreDto> getStoreInfo(Long partnerId) {
        PartnerEntity partnerEntity = partnerRepository.findById(partnerId).orElseThrow(
                () -> new RuntimeException("Partner not found")
        );

        List<StoreDto> result = storeRepository.findAllByPartnerIdOrderByNameAsc(partnerEntity.getId())
                .stream()
                .map(storeEntity -> StoreDto.from(storeEntity)).collect(Collectors.toList());

        log.info("store list ----> {}", result);

        return result;
    }

    //수정



    //삭제


}
