package com.eunhasoo.bookclub.exception.review;

import com.eunhasoo.bookclub.exception.BookClubException;

public class ReviewAlreadyExistException extends BookClubException {

    private static final String REVIEW_ALREADY_EXIST = "이미 해당 책에 대한 리뷰가 존재합니다.";

    public ReviewAlreadyExistException() {
        super(REVIEW_ALREADY_EXIST);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
