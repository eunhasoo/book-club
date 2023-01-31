package com.eunhasoo.bookclub.exception.review;

import com.eunhasoo.bookclub.exception.BookClubException;

public class ReviewNotFoundException extends BookClubException {

    private static final String REVIEW_NOT_FOUND = "리뷰를 찾을 수 없습니다.";

    public ReviewNotFoundException(Long reviewId) {
        super(String.format("%s -> [id: %d]", REVIEW_NOT_FOUND, reviewId));
    }

    @Override
    public int getStatusCode() {
        return 404;
    }
}
