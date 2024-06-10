package com.zerobase.reserve.service.store;

import com.zerobase.domain.dto.StoreDto;
import com.zerobase.domain.entity.StoreEntity;
import com.zerobase.domain.exception.CustomException;
import com.zerobase.domain.exception.ErrorCode;
import com.zerobase.domain.repository.StoreRepository;
import com.zerobase.domain.requestForm.store.UpdateStoreForm;
import com.zerobase.reserve.service.CustomerSearchService;
import com.zerobase.reserve.service.StoreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
class StoreServiceUpdateAndDeleteTest {

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private CustomerSearchService customerSearchService;

    @InjectMocks
    private StoreService storeService;

    private StoreEntity storeEntity;

    @BeforeEach
    void setUp() {
        StoreEntity.StoreDetailEntity storeDetailEntity =
                StoreEntity.StoreDetailEntity.builder()
                        .address("address")
                        .build();
        storeEntity = StoreEntity.builder()
                .id(1L)
                .partnerId(1L)
                .name("Test Store")
                .description("Test Description")
                .latitude(0.0)
                .longitude(0.0)
                .reservePossible(true)
                .storeDetail(storeDetailEntity)
                .build();
    }

    @Test
    void updateStoreTest() {
        // given
        UpdateStoreForm.UpdateStoreDetailForm updateStoreDetailForm =
                UpdateStoreForm.UpdateStoreDetailForm.builder()
                        .address("address")
                        .build();
        UpdateStoreForm updateStoreForm = UpdateStoreForm.builder()
                .storeId(1L)
                .partnerId(1L)
                .storeName("Updated Store")
                .description("Updated Description")
                .latitude(1.0)
                .longitude(1.0)
                .reservePossible(false)
                .detailForm(updateStoreDetailForm)
                .build();



        given(storeRepository.findById(anyLong())).willReturn(Optional.of(storeEntity));
        given(storeRepository.save(any(StoreEntity.class))).willReturn(storeEntity);

        // when
        StoreDto updatedStore = storeService.updateStore(1L, updateStoreForm);

        // then
        assertEquals("Updated Store", updatedStore.getName());
        assertEquals("Updated Description", updatedStore.getDescription());
        assertEquals(1.0, updatedStore.getLatitude());
        assertEquals(1.0, updatedStore.getLongitude());
        assertEquals(false, updatedStore.isReservePossible());
    }

    @Test
    void updateStoreUnauthorizedTest() {
        // given
        UpdateStoreForm updateStoreForm = UpdateStoreForm.builder()
                .storeId(1L)
                .partnerId(2L)
                .storeName("Updated Store")
                .description("Updated Description")
                .latitude(1.0)
                .longitude(1.0)
                .reservePossible(false)
                .build();

        given(storeRepository.findById(anyLong())).willReturn(Optional.of(storeEntity));

        // when
        // then
        CustomException exception = assertThrows(CustomException.class, () -> {
            storeService.updateStore(2L, updateStoreForm);
        });
        assertEquals(ErrorCode.NOT_AUTH_UPDATE_STORE, exception.getErrorCode());
    }

    @Test
    void deleteStoreTest() {
        Long partnerId = 1L;
        StoreEntity storeEntity1 = StoreEntity.builder()
                .id(1L)
                .name("Store 1")
                .partnerId(partnerId)
                .build();
        StoreEntity storeEntity2 = StoreEntity.builder()
                .id(2L)
                .name("Store 2")
                .partnerId(partnerId)
                .build();
        StoreEntity storeEntity3 = StoreEntity.builder()
                .id(3L)
                .name("Store 3")
                .partnerId(partnerId)
                .build();

        given(storeRepository.findById(1L)).willReturn(Optional.of(storeEntity1));
        given(storeRepository.findById(2L)).willReturn(Optional.of(storeEntity2));
        given(storeRepository.findById(3L)).willReturn(Optional.of(storeEntity3));

        doNothing().when(storeRepository).delete(any(StoreEntity.class));
        doNothing().when(customerSearchService).deleteAutoCompleteKeywords(anyString());

        // when
        int count = storeService.deleteStore(partnerId, Arrays.asList(1L, 2L, 3L));

        // then
        assertEquals(3, count);
    }

    @Test
    void deleteStoreUnauthorizedTest() {
        // given
        given(storeRepository.findById(anyLong())).willReturn(Optional.of(storeEntity));

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> {
            storeService.deleteStore(2L, Arrays.asList(1L));
        });
        assertEquals(ErrorCode.NOT_AUTH_DELETE_STORE, exception.getErrorCode());
    }
}