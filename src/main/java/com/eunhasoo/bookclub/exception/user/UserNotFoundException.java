package com.eunhasoo.bookclub.exception.user;

import com.eunhasoo.bookclub.exception.BookClubException;

public class UserNotFoundException extends BookClubException {

    private static final String USER_NOT_FOUND_MESSAGE = "회원 정보를 찾을 수 없습니다.";

    public UserNotFoundException(Long id) {
        super(String.format("%s -> [userId: %d]", USER_NOT_FOUND_MESSAGE, id));
    }

    public UserNotFoundException(String username) {
        super(String.format("%s -> [username: %s]", USER_NOT_FOUND_MESSAGE, username));
    }

    @Override
    public int getStatusCode() {
        return 404;
    }
}
