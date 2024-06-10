package com.zerobase.domain.requestForm.store;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateStoreForm {
    private Long storeId;
    private Long partnerId;
    private String storeName;
    private Double latitude;
    private Double longitude;
    private String description;
    private boolean reservePossible;
    private UpdateStoreDetailForm detailForm;


    @Getter
    @Builder
    public static class UpdateStoreDetailForm {
        private String tel;
        private String address;
        private String description;
        private LocalDateTime openTime;
        private LocalDateTime closeTime;
    }
}
