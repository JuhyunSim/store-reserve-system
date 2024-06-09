package com.zerobase.domain.dto;

import com.zerobase.domain.entity.ReviewEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ReviewDto {

    private Long id;
    private Long customerId;
    private Long storeId;
    private Long partnerId;
    private String title;
    private String content;
    private int rating;

    public static ReviewDto from(ReviewEntity reviewEntity) {
        return ReviewDto.builder()
                .id(reviewEntity.getId())
                .customerId(reviewEntity.getCustomerId())
                .storeId(reviewEntity.getStoreId())
                .partnerId(reviewEntity.getPartnerId())
                .title(reviewEntity.getTitle())
                .content(reviewEntity.getContent())
                .rating(reviewEntity.getRating())
                .build();

    }
}
