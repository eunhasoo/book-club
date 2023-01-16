package com.eunhasoo.bookclub.exception.user;

import com.eunhasoo.bookclub.exception.BookClubException;

public class DuplicatedEmailException extends BookClubException {

    private static final String MESSAGE = "이미 존재하는 이메일입니다.";

    public DuplicatedEmailException(String email) {
        super(String.format("%s -> [email: %s]", MESSAGE, email));
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
