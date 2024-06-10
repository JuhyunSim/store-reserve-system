package com.zerobase.domain.dto;


import com.zerobase.domain.entity.StoreEntity;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreDto {
    private Long id;
    private Long partnerId;
    private String name;
    private Double latitude;
    private Double longitude;
    private String description;
    private boolean reservePossible;
    private StoreDetailDto storeDetailDto;


    public static StoreDto from(StoreEntity storeEntity) {
        return StoreDto.builder()
                .id(storeEntity.getId())
                .partnerId(storeEntity.getPartnerId())
                .name(storeEntity.getName())
                .latitude(storeEntity.getLatitude())
                .longitude(storeEntity.getLongitude())
                .description(storeEntity.getDescription())
                .reservePossible(storeEntity.isReservePossible())
                .storeDetailDto(StoreDetailDto.from(storeEntity.getStoreDetail()))
                .build();
    }


    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StoreDetailDto {
        private Long id;

        private String tel;
        private String address;
        private String description;
        private LocalDateTime openTime;
        private LocalDateTime closeTime;

        public static StoreDetailDto from(StoreEntity.StoreDetailEntity storeDetailEntity) {
            return StoreDetailDto.builder()
                    .id(storeDetailEntity.getId())
                    .tel(storeDetailEntity.getTel())
                    .address(storeDetailEntity.getAddress())
                    .description(storeDetailEntity.getDescription())
                    .openTime(storeDetailEntity.getOpenTime())
                    .closeTime(storeDetailEntity.getCloseTime())
                    .build();
        }
    }
}
