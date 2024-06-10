package com.zerobase.domain.requestForm.store;

import com.zerobase.domain.entity.StoreEntity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreForm {
    private Long partnerId;
    private String storeName;
    private Double latitude;
    private Double longitude;
    private String description;

    private StoreDetailForm storeDetailForm;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StoreDetailForm {
        private String tel;
        private String address;
        private String description;
        private LocalDateTime openTime;
        private LocalDateTime closeTime;
    }
}
