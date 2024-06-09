package com.zerobase.domain.requestForm.store;

import lombok.*;

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
}
