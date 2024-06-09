package com.zerobase.domain.requestForm;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateReviewForm {
    @NotNull(message = "Review ID is required")
    private Long reviewId;
    private Long reviewerId;
    private Long storeId;
    private Long partnerId;
    private String content;
    private String title;
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Integer rating;

    public void setTitle(String content) {
        if (content != null && content.length() >= 15) {
            this.title = content.substring(0, 15);
        } else {
            this.title = content;
        }
    }

}
