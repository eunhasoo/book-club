package com.eunhasoo.bookclub.review.ui;

import com.eunhasoo.bookclub.auth.CurrentUser;
import com.eunhasoo.bookclub.common.ResultList;
import com.eunhasoo.bookclub.review.application.CommentService;
import com.eunhasoo.bookclub.review.application.ReviewService;
import com.eunhasoo.bookclub.review.domain.Review;
import com.eunhasoo.bookclub.review.ui.request.CommentSearch;
import com.eunhasoo.bookclub.review.ui.request.ReviewCreate;
import com.eunhasoo.bookclub.review.ui.request.ReviewSearch;
import com.eunhasoo.bookclub.review.ui.request.ReviewUpdate;
import com.eunhasoo.bookclub.review.ui.response.CommentListResponse;
import com.eunhasoo.bookclub.review.ui.response.ReviewListResponse;
import com.eunhasoo.bookclub.review.ui.response.ReviewResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final CommentService commentService;

    public ReviewController(ReviewService reviewService, CommentService commentService) {
        this.reviewService = reviewService;
        this.commentService = commentService;
    }

    @GetMapping("/{reviewId}")
    public ReviewResponse get(@PathVariable Long reviewId) {
        Review review = reviewService.getReview(reviewId);
        return new ReviewResponse(review);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void write(@CurrentUser Long userId, @RequestBody @Valid ReviewCreate reviewCreate) {
        reviewService.write(userId, reviewCreate);
    }

    @PostMapping("/{reviewId}")
    public void update(@CurrentUser Long userId, @PathVariable Long reviewId, @RequestBody @Valid ReviewUpdate reviewUpdate) {
        reviewService.update(userId, reviewId, reviewUpdate);
    }

    @GetMapping
    public ResultList<?> reviews(@ModelAttribute ReviewSearch reviewSearch) {
        List<ReviewListResponse> reviews = reviewService.getReviews(reviewSearch)
                .stream()
                .map(ReviewListResponse::new)
                .collect(Collectors.toUnmodifiableList());

        return new ResultList(reviews.size(), reviews);
    }

    @DeleteMapping("/{reviewId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@CurrentUser Long userId, @PathVariable Long reviewId) {
        reviewService.delete(userId, reviewId);
    }

    @GetMapping("/{reviewId}/comments")
    public ResultList<?> getComments(@PathVariable Long reviewId, CommentSearch commentSearch) {
        List<CommentListResponse> result = commentService.getComments(reviewId, commentSearch)
                .stream()
                .map(CommentListResponse::new)
                .collect(Collectors.toUnmodifiableList());

        return new ResultList(result.size(), result);
    }
}
