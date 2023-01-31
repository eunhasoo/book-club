package com.eunhasoo.bookclub.review.domain;

import com.eunhasoo.bookclub.exception.auth.DataAccessFailureException;
import com.eunhasoo.bookclub.exception.review.ReviewNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewRepositoryCustom {

    Boolean existsByReviewerIdAndBookInfoId(Long reviewerId, Long bookInfoId);

    Optional<Review> findByIdAndReviewerId(Long reviewId, Long reviewerId);

    default Review getById(Long reviewId) {
        return findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(reviewId));
    }

    default Review getByIdAndReviewerId(Long reviewId, Long reviewerId) {
        return findByIdAndReviewerId(reviewId, reviewerId)
                .orElseThrow(() -> new DataAccessFailureException());
    }
}
