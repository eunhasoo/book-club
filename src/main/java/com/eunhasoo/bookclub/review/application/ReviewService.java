package com.eunhasoo.bookclub.review.application;

import com.eunhasoo.bookclub.book.domain.BookInfo;
import com.eunhasoo.bookclub.book.domain.BookInfoRepository;
import com.eunhasoo.bookclub.exception.review.ReviewAlreadyExistException;
import com.eunhasoo.bookclub.review.domain.CommentRepository;
import com.eunhasoo.bookclub.review.domain.Review;
import com.eunhasoo.bookclub.review.domain.ReviewRepository;
import com.eunhasoo.bookclub.review.ui.request.ReviewCreate;
import com.eunhasoo.bookclub.review.ui.request.ReviewSearch;
import com.eunhasoo.bookclub.review.ui.request.ReviewUpdate;
import com.eunhasoo.bookclub.user.domain.User;
import com.eunhasoo.bookclub.user.domain.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookInfoRepository bookInfoRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    public ReviewService(ReviewRepository reviewRepository,
                         BookInfoRepository bookInfoRepository,
                         UserRepository userRepository,
                         CommentRepository commentRepository) {
        this.reviewRepository = reviewRepository;
        this.bookInfoRepository = bookInfoRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
    }

    @Transactional
    public Long write(Long userId, ReviewCreate reviewCreate) {
        BookInfo bookInfo = bookInfoRepository.getById(reviewCreate.getBookInfoId());
        User reviewer = userRepository.getById(userId);

        checkIfReviewHasAlreadyWritten(reviewer, bookInfo);

        Review review = Review.builder()
                .title(reviewCreate.getTitle())
                .content(reviewCreate.getContent())
                .score(reviewCreate.getScore())
                .reviewer(reviewer)
                .bookInfo(bookInfo)
                .build();

        reviewRepository.save(review);

        return review.getId();
    }

    private void checkIfReviewHasAlreadyWritten(User reviewer, BookInfo bookInfo) {
        if (reviewRepository.existsByReviewerIdAndBookInfoId(reviewer.getId(), bookInfo.getId())) {
            throw new ReviewAlreadyExistException();
        }
    }

    public List<Review> getReviews(ReviewSearch reviewSearch) {
        return reviewRepository.getList(reviewSearch);
    }

    public Review getReview(Long reviewId) {
        return reviewRepository.getById(reviewId);
    }

    @Transactional
    public void update(Long userId, Long reviewId, ReviewUpdate reviewUpdate) {
        Review review = reviewRepository.getByIdAndReviewerId(reviewId, userId);
        review.update(reviewUpdate.getTitle(), reviewUpdate.getContent());
    }

    @Transactional
    public void delete(Long userId, Long reviewId) {
        Review review = reviewRepository.getByIdAndReviewerId(reviewId, userId);

        commentRepository.deleteAllByReviewId(reviewId);
        reviewRepository.delete(review);
    }
}
