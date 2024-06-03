package com.zerobase.partner.domain;

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
