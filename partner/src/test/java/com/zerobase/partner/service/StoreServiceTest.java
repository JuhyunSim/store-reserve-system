package com.zerobase.partner.service;

import com.zerobase.partner.domain.dto.StoreDto;
import com.zerobase.partner.domain.model.PartnerEntity;
import com.zerobase.partner.domain.model.StoreEntity;
import com.zerobase.partner.domain.repository.PartnerRepository;
import com.zerobase.partner.domain.repository.StoreRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class StoreServiceTest {

    @Mock
    PartnerRepository partnerRepository;
    @Mock
    StoreRepository storeRepository;

    @InjectMocks
    StoreService storeService;

    @Test
    void getStoreInfoTest() {
        //given
        String email = "111@g2222";
        StoreEntity store1 = StoreEntity.builder()
                .partnerId(1L)
                .name("a")
                .longitude(1.11)
                .latitude(2.22)
                .description("a입니다.")
                .build();

        StoreEntity store2 = StoreEntity.builder()
                .partnerId(1L)
                .name("b")
                .longitude(1.11)
                .latitude(2.22)
                .description("b입니다.")
                .build();

         PartnerEntity partner = PartnerEntity.builder().id(1L).build();

        given(partnerRepository.findByEmail(anyString())).willReturn(
                Optional.ofNullable(partner)
        );
        given(storeRepository.findAllByPartnerIdOrderByNameAsc(anyLong()))
                .willReturn(List.of(store1, store2));

        //when
        List<StoreDto> result = storeService.getStoreInfo(email);

        //then
        assertEquals(2, result.size());
        assertEquals("a", result.get(0).getName());
        assertEquals("b", result.get(1).getName());
    }


}