package com.eunhasoo.bookclub.exception.user;

import com.eunhasoo.bookclub.exception.BookClubException;

public class DuplicatedUsernameException extends BookClubException {

    private static final String MESSAGE = "이미 존재하는 아이디입니다.";

    public DuplicatedUsernameException(String username) {
        super(String.format("%s -> [username: %s]", MESSAGE, username));
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
