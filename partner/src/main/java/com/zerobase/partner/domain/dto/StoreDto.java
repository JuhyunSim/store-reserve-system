package com.zerobase.partner.domain.dto;


import com.zerobase.partner.domain.model.StoreEntity;
import lombok.*;

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


    public static StoreDto from(StoreEntity storeEntity) {
        return StoreDto.builder()
                .id(storeEntity.getId())
                .partnerId(storeEntity.getPartnerId())
                .name(storeEntity.getName())
                .latitude(storeEntity.getLatitude())
                .longitude(storeEntity.getLongitude())
                .description(storeEntity.getDescription())
                .build();
    }
}
