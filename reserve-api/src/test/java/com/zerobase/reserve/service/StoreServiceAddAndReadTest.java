package com.zerobase.reserve.service;

import com.zerobase.domain.dto.StoreDto;
import com.zerobase.domain.entity.PartnerEntity;
import com.zerobase.domain.entity.StoreEntity;
import com.zerobase.domain.exception.CustomException;
import com.zerobase.domain.exception.ErrorCode;
import com.zerobase.domain.repository.PartnerRepository;
import com.zerobase.domain.repository.StoreRepository;
import com.zerobase.domain.requestForm.store.StoreForm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class StoreServiceAddAndReadTest {

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private PartnerRepository partnerRepository;

    @InjectMocks
    private StoreService storeService;

    private StoreForm storeForm;
    private StoreEntity storeEntity;


    @Test
    void addStoreSuccessTest() {
        //given
        storeForm = StoreForm.builder()
                .partnerId(1L)
                .storeName("Test Store")
                .description("Test Description")
                .latitude(0.0)
                .longitude(0.0)
                .build();

        storeEntity = StoreEntity.builder()
                .id(1L)
                .partnerId(1L)
                .name("Test Store")
                .description("Test Description")
                .latitude(0.0)
                .longitude(0.0)
                .reservePossible(true)
                .build();
        given(storeRepository.existsByPartnerIdAndName(
                anyLong(), any(String.class))).willReturn(false);
        given(storeRepository.save(any(StoreEntity.class)))
                .willReturn(storeEntity);

        //when
        StoreDto storeDto = storeService.addStore(storeForm);

        //then
        assertNotNull(storeDto);
        assertEquals("Test Store", storeDto.getName());
        assertEquals("Test Description", storeDto.getDescription());
        assertEquals(0.0, storeDto.getLatitude(), 0.001);
        assertEquals(0.0, storeDto.getLongitude(), 0.001);
        assertTrue(storeDto.isReservePossible());
    }

    @Test
    void addStoreFailTest_AlreadyExist() {
        //given
        storeForm = StoreForm.builder()
                .partnerId(1L)
                .storeName("Test Store")
                .description("Test Description")
                .latitude(0.0)
                .longitude(0.0)
                .build();

        storeEntity = StoreEntity.builder()
                .id(1L)
                .partnerId(1L)
                .name("Test Store")
                .description("Test Description")
                .latitude(0.0)
                .longitude(0.0)
                .reservePossible(true)
                .build();

        given(storeRepository.existsByPartnerIdAndName(
                anyLong(), any(String.class))).willReturn(true);
        //when
        CustomException customException = assertThrows(CustomException.class,
                () -> storeService.addStore(storeForm));

        //then
        assertEquals(ErrorCode.ALREADY_EXIST_STORE, customException.getErrorCode());
    }

    @Test
    void getStoreInfoTest() {
        // given
        storeEntity = StoreEntity.builder()
                .id(1L)
                .partnerId(1L)
                .name("Test Store")
                .description("Test Description")
                .latitude(0.0)
                .longitude(0.0)
                .reservePossible(true)
                .build();

        PartnerEntity partnerEntity = PartnerEntity.builder().id(1L).build();
        given(partnerRepository.findById(anyLong()))
                .willReturn(Optional.of(partnerEntity));
        given(storeRepository.findAllByPartnerIdOrderByNameAsc(anyLong()))
                .willReturn(Arrays.asList(storeEntity));

        // when
        List<StoreDto> storeDtos = storeService.getStoreInfo(1L);

        // then
        assertEquals(1, storeDtos.size());
        assertEquals("Test Store", storeDtos.get(0).getName());
        assertEquals("Test Description", storeDtos.get(0).getDescription());
        assertEquals(0.0, storeDtos.get(0).getLatitude(), 0.001);
        assertEquals(0.0, storeDtos.get(0).getLongitude(), 0.001);
        assertTrue(storeDtos.get(0).isReservePossible());
    }

    @Test
    void getStoreInfoPartnerNotFoundTest() {
        // given
        given(partnerRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> {
            storeService.getStoreInfo(1L);
        });
        assertEquals(ErrorCode.NOT_FOUND_USER, exception.getErrorCode());
    }
}