package com.zerobase.domain.entity;

import com.zerobase.domain.requestForm.ReviewForm;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

@Entity(name = "review")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long storeId;
    private Long customerId;
    private Long partnerId;
    private String title;
    private String content;

    @Min(1)
    @Max(5)
    private int rating;

    public static ReviewEntity from(ReviewForm reviewForm) {
        reviewForm.setTitle(reviewForm.getContent());
        return ReviewEntity.builder()
                .customerId(reviewForm.getCustomerId())
                .storeId(reviewForm.getStoreId())
                .partnerId(reviewForm.getPartnerId())
                .title(reviewForm.getTitle())
                .content(reviewForm.getContent())
                .rating(reviewForm.getRating())
                .build();
    }
}
