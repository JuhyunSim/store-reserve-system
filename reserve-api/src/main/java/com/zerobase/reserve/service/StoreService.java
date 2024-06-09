package com.zerobase.reserve.service;

import com.zerobase.domain.dto.StoreDto;
import com.zerobase.domain.entity.PartnerEntity;
import com.zerobase.domain.entity.StoreEntity;
import com.zerobase.domain.exception.CustomException;
import com.zerobase.domain.exception.ErrorCode;
import com.zerobase.domain.repository.PartnerRepository;
import com.zerobase.domain.repository.StoreRepository;
import com.zerobase.domain.requestForm.store.StoreForm;
import com.zerobase.domain.requestForm.store.UpdateStoreForm;
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
    public StoreDto addStore(StoreForm storeForm) {
        validateAddStore(storeForm);
        StoreEntity storeEntity = StoreEntity.of(
                storeForm.getPartnerId(), storeForm);

        return StoreDto.from(storeRepository.save(storeEntity));
    }

    private void validateAddStore(StoreForm storeForm) {
        //partnerId와 storeName을 기준으로 이미 있으면 추가 제외
        boolean exist = storeRepository.existsByPartnerIdAndName(
                storeForm.getPartnerId(), storeForm.getStoreName());
        if (exist) {
            throw new CustomException(ErrorCode.ALREADY_EXIST_STORE);
        }
    }

    //조회(자기 아이디로 조회 가능)
    public List<StoreDto> getStoreInfo(Long partnerId) {
        PartnerEntity partnerEntity = partnerRepository.findById(partnerId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER)
        );

        List<StoreDto> result = storeRepository.findAllByPartnerIdOrderByNameAsc(partnerEntity.getId())
                .stream()
                .map(StoreDto::from).collect(Collectors.toList());

        log.info("store list ----> {}", result);

        return result;
    }

    //수정
    public StoreDto updateStore(Long partnerId, UpdateStoreForm updateStoreForm) {

        StoreEntity storeEntity = validateUpdateStore(partnerId, updateStoreForm);

        storeEntity.setDescription(updateStoreForm.getDescription());
        storeEntity.setName(updateStoreForm.getStoreName());
        storeEntity.setLongitude(updateStoreForm.getLongitude());
        storeEntity.setLatitude(updateStoreForm.getLatitude());
        storeEntity.setReservePossible(updateStoreForm.isReservePossible());
        return StoreDto.from(storeRepository.save(storeEntity));
    }

    private StoreEntity validateUpdateStore(
            Long partnerId, UpdateStoreForm updateStoreForm
    ) {
        StoreEntity storeEntity =
                storeRepository.findById(updateStoreForm.getStoreId())
                        .orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_STORE)
        );

        if (!storeEntity.getPartnerId().equals(partnerId)) {
            throw new CustomException(ErrorCode.NOT_AUTH_UPDATE_STORE);
        }

        return storeEntity;
    }

    //삭제
    public int deleteStore(Long partnerId, List<Long> storeIds) {
        int count = 0;
        for(Long storeId : storeIds) {

            StoreEntity storeEntity =
                    validateDeleteStore(partnerId, storeId);

            try {
                storeRepository.delete(storeEntity);
            } catch (Exception e) {
                throw new CustomException(ErrorCode.FAIL_TO_DELETE_STORE);
            }
            count++;
        }
        return count;
    }

    private StoreEntity validateDeleteStore(Long partnerId, Long storeId) {
        StoreEntity storeEntity = storeRepository.findById(storeId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_STORE)
        );

        if (!storeEntity.getPartnerId().equals(partnerId)) {
            throw new CustomException(ErrorCode.NOT_AUTH_DELETE_STORE);
        }
        return storeEntity;
    }

}
