package com.eunhasoo.bookclub.exception.auth;

import com.eunhasoo.bookclub.exception.BookClubException;

public class DataAccessFailureException extends BookClubException {

    private static final String DATA_ACCESS_FAIL = "해당 데이터를 찾을 수 없거나 접근 권한이 없습니다.";

    public DataAccessFailureException() {
        super(DATA_ACCESS_FAIL);
    }

    @Override
    public int getStatusCode() {
        return 403;
    }
}
