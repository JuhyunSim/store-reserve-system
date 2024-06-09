package com.zerobase.reserve.service;

import com.zerobase.domain.dto.ReviewDto;
import com.zerobase.domain.entity.ReviewEntity;
import com.zerobase.domain.exception.CustomException;
import com.zerobase.domain.exception.ErrorCode;
import com.zerobase.domain.repository.ReserveRepository;
import com.zerobase.domain.repository.ReviewRepository;
import com.zerobase.domain.requestForm.ReviewForm;
import com.zerobase.domain.requestForm.UpdateReviewForm;
import com.zerobase.domain.security.common.UserType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.zerobase.domain.exception.ErrorCode.NOT_FOUND_REVIEW;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReserveRepository reserveRepository;

    public ReviewDto addReview(Long customerId, ReviewForm reviewForm) {
        //리뷰를 요청한 customer가 reviewform에 있는 store를 사용한 기록이 있는가?(reserveRepository)
        validateAddReview(customerId, reviewForm);
        ReviewEntity reviewEntity = ReviewEntity.from(reviewForm);
        ReviewEntity savedReview = reviewRepository.save(reviewEntity);

        return ReviewDto.from(savedReview);
    }

    private void validateAddReview(Long customerId, ReviewForm reviewForm) {
        boolean qualified = reserveRepository
                .findAllByCustomerId(customerId).stream()
                .anyMatch(reserve ->
                        reserve.getStoreId()
                                .equals(reviewForm.getStoreId()) &&
                                reserve.isConfirm()
                );
        if (!qualified) {
            throw new CustomException(ErrorCode.NOT_AUTH_REVIEW);
        }
        if (!customerId.equals(reviewForm.getCustomerId())) {
            throw new CustomException(ErrorCode.NOT_AUTH_REVIEW);
        }
    }

    public ReviewDto updateReview(
            UserType userType, Long userId, UpdateReviewForm updateReviewForm
    ) {
        validateUpdate(userType,userId, updateReviewForm);
        ReviewEntity reviewEntity = reviewRepository
                .findById(updateReviewForm.getReviewId()).orElseThrow(
                () -> new CustomException(NOT_FOUND_REVIEW)
        );

        reviewEntity.setContent(updateReviewForm.getContent());
        reviewEntity.setTitle(updateReviewForm.getContent());  // 제목 자동 수정
        reviewEntity.setRating(updateReviewForm.getRating());

        ReviewEntity updatedReview = reviewRepository.save(reviewEntity);

        return ReviewDto.from(updatedReview);
    }

    private void validateUpdate(UserType userType,
                                Long userId,
                                UpdateReviewForm updateReviewForm) {
        if (!(userType.equals(UserType.CUSTOMER) &&
                userId.equals(updateReviewForm.getReviewerId()))
                &&
                !(userType.equals(UserType.PARTNER) &&
                        userId.equals(updateReviewForm.getPartnerId()))
        ) {
            throw new CustomException(ErrorCode.NOT_AUTH_UPDATE_REVIEW);
        }

        if (updateReviewForm.getContent() == null) {
            throw new CustomException(ErrorCode.NO_TEXT);
        }

        if (updateReviewForm.getRating() == null) {
            throw new CustomException(ErrorCode.NO_RATING);
        }
    }

    public ReviewDto getReview(Long reviewId) {

        ReviewEntity reviewEntity = reviewRepository.findById(reviewId).orElseThrow(
                () -> new CustomException(NOT_FOUND_REVIEW)
        );
        return ReviewDto.from(reviewEntity);
    }

    public List<ReviewDto> getReviewList(Long storeId) {

        return reviewRepository.findAllByStoreId(storeId).stream()
                        .map(ReviewDto::from).collect(Collectors.toList());
    }

    public int deleteReview(UserType userType, Long userId, Long reviewId) {
        int count = 0;
        ReviewEntity reviewEntity = reviewRepository.findById(reviewId).orElseThrow(
                () -> new CustomException(NOT_FOUND_REVIEW)
        );
        count += getDeleteCount(userType, userId, reviewEntity);

        return count;
    }



    public int deleteReviews(
            UserType userType, Long userId, List<Long> reviewIds
    ) {
        int count = 0;
        for (Long reviewId : reviewIds) {
            try {
                ReviewEntity reviewEntity =
                        reviewRepository.findById(reviewId).orElseThrow(
                        () -> new CustomException(NOT_FOUND_REVIEW)
                );
                count += getDeleteCount(userType, userId, reviewEntity);
            } catch (Exception e) {
                log.info("Delete review with id <{}> ) failed", reviewId);
                throw new CustomException(NOT_FOUND_REVIEW);
            }
        }
        return count;
    }

    private int getDeleteCount(UserType userType, Long userId, ReviewEntity reviewEntity) {
        int count = 0;
        if (userType.equals(UserType.PARTNER)) {
            count += getDeleteCountByPartnerId(reviewEntity, userId);
        } else if (userType.equals(UserType.CUSTOMER)) {
            count += getDeleteCountByCustomerId(reviewEntity, userId);
        }
        return count;
    }
    private int getDeleteCountByPartnerId(ReviewEntity reviewEntity, Long partnerId) {
        int count = 0;
        if (partnerId.equals(reviewEntity.getPartnerId())) {
            try {
                reviewRepository.deleteById(reviewEntity.getId());
                count += 1;
            } catch (Exception e) {
                throw new CustomException(NOT_FOUND_REVIEW);
            }
        }
        return count;
    }
    private int getDeleteCountByCustomerId(ReviewEntity reviewEntity, Long customerId) {
        int count = 0;
        if (customerId.equals(reviewEntity.getCustomerId())) {
            try {
                reviewRepository.deleteById(reviewEntity.getId());
                count += 1;
            } catch (Exception e) {
                throw new CustomException(NOT_FOUND_REVIEW);
            }
        }
        return count;
    }
}

