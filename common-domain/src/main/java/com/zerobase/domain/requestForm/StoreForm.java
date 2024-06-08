package com.zerobase.domain.requestForm;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreForm {
    private String storeName;
    private Double latitude;
    private Double longitude;
    private String description;
}