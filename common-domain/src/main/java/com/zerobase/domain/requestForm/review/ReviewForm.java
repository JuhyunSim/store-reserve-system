package com.zerobase.domain.requestForm.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewForm {
    private Long storeId;
    private Long customerId;
    private Long partnerId;

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private int rating;
    private String content;
    private String title;

    public void setTitle(String content) {
        if (content != null && content.length() >= 15) {
            this.title = content.substring(0, 15);
        } else {
            this.title = content;
        }
    }
}
