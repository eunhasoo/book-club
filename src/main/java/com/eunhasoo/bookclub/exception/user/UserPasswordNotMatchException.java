package com.eunhasoo.bookclub.exception.user;

import com.eunhasoo.bookclub.exception.BookClubException;

public class UserPasswordNotMatchException extends BookClubException {

    private static final String MESSAGE = "입력한 값이 현재 비밀번호와 일치하지 않습니다.";

    public UserPasswordNotMatchException(Long id) {
        super(String.format("%s -> [userId: %d]", MESSAGE, id));
    }

    @Override
    public int getStatusCode() {
        return 401;
    }
}
