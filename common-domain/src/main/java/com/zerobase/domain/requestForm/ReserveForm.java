package com.zerobase.domain.requestForm;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReserveForm {

    private Long storeId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String storeName;
    private Double latitude;
    private Double longitude;
    private String storePhone;
}
