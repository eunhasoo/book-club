package com.eunhasoo.bookclub.review.domain;

import com.eunhasoo.bookclub.exception.review.CommentNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {

    @Modifying
    @Query("delete from Comment c where c.review.id = :reviewId")
    void deleteAllByReviewId(Long reviewId);

    Optional<Comment> findByIdAndUserId(Long id, Long userId);

    default Comment getByIdAndUserId(Long id, Long userId) {
        return findByIdAndUserId(id, userId)
                .orElseThrow(() -> new CommentNotFoundException(id, userId));
    }
}
