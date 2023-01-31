package com.eunhasoo.bookclub.review.ui.response;

import com.eunhasoo.bookclub.review.domain.Review;
import lombok.Getter;

import java.util.List;

@Getter
public class ReviewResponse {
    private Long reviewId;
    private String reviewer;
    private int score;
    private String title;
    private String content;

    public ReviewResponse(Review review) {
        this.reviewId = review.getId();
        this.reviewer = review.getReviewer().getNickname();
        this.score = review.getScore();
        this.title = review.getTitle();
        this.content = review.getContent();
    }
}
