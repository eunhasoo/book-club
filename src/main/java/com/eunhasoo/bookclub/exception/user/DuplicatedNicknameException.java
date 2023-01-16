package com.eunhasoo.bookclub.exception.user;

import com.eunhasoo.bookclub.exception.BookClubException;

public class DuplicatedNicknameException extends BookClubException {

    private static final String MESSAGE = "이미 존재하는 닉네임입니다.";

    public DuplicatedNicknameException(String nickname) {
        super(String.format("%s -> [nickname: %s]", MESSAGE, nickname));
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
