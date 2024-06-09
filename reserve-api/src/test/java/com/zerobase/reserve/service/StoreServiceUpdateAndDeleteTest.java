package com.zerobase.reserve.service;

import com.zerobase.domain.dto.StoreDto;
import com.zerobase.domain.entity.StoreEntity;
import com.zerobase.domain.exception.CustomException;
import com.zerobase.domain.exception.ErrorCode;
import com.zerobase.domain.repository.StoreRepository;
import com.zerobase.domain.requestForm.store.UpdateStoreForm;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
class StoreServiceUpdateAndDeleteTest {

    @Mock
    private StoreRepository storeRepository;

    @InjectMocks
    private StoreService storeService;

    private StoreEntity storeEntity;

    @BeforeEach
    void setUp() {
        storeEntity = StoreEntity.builder()
                .id(1L)
                .partnerId(1L)
                .name("Test Store")
                .description("Test Description")
                .latitude(0.0)
                .longitude(0.0)
                .reservePossible(true)
                .build();
    }

    @Test
    void updateStoreTest() {
        // given
        UpdateStoreForm updateStoreForm = UpdateStoreForm.builder()
                .storeId(1L)
                .partnerId(1L)
                .storeName("Updated Store")
                .description("Updated Description")
                .latitude(1.0)
                .longitude(1.0)
                .reservePossible(false)
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
        // given
        given(storeRepository.findById(anyLong())).willReturn(Optional.of(storeEntity));
        doNothing().when(storeRepository).delete(any(StoreEntity.class));

        // when
        int count = storeService.deleteStore(1L, Arrays.asList(1L, 2L, 3L));

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