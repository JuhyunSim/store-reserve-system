package com.zerobase.partner.service;

import com.zerobase.partner.domain.StoreForm;
import com.zerobase.partner.domain.dto.StoreDto;
import com.zerobase.partner.domain.model.PartnerEntity;
import com.zerobase.partner.domain.model.StoreEntity;
import com.zerobase.partner.domain.repository.PartnerRepository;
import com.zerobase.partner.domain.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PartnerStoreService {

    private final StoreRepository storeRepository;
    private final PartnerRepository partnerRepository;

    //등록
    public StoreDto addStore(Long partnerId, StoreForm storeForm) {
        //partnerId와 storeName을 기준으로 이미 있으면 추가 제외
        boolean exist =
                storeRepository.findAllByPartnerIdAndName(
                        partnerId, storeForm.getStoreName());
        if (exist) {
            throw new RuntimeException("Store Already Exists");
        }

        return StoreDto.from(
                storeRepository.save(StoreEntity.of(partnerId, storeForm))
        );
    }

    //조회
    public List<StoreDto> getStoreInfo(String email) {
        PartnerEntity partnerEntity = partnerRepository.findByEmail(email).orElseThrow(
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
