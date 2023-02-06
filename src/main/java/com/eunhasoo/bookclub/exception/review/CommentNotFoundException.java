package com.eunhasoo.bookclub.exception.review;

import com.eunhasoo.bookclub.exception.BookClubException;

public class CommentNotFoundException extends BookClubException {

    private static final String COMMENT_NOT_FOUND = "댓글을 찾을 수 없습니다.";

    public CommentNotFoundException(Long id, Long userId) {
        super(String.format("%s -> [id: %d, userId: %d]", COMMENT_NOT_FOUND, id, userId));
    }

    @Override
    public int getStatusCode() {
        return 404;
    }
}
