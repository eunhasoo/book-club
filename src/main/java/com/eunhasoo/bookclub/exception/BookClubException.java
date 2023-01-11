package com.eunhasoo.bookclub.exception;

public abstract class BookClubException extends RuntimeException {

    public BookClubException(String message) {
        super(message);
    }

    public BookClubException(String message, Throwable cause) {
        super(message, cause);
    }

    public abstract int getStatusCode();
}
