package com.eunhasoo.bookclub.user.domain;

import lombok.Getter;

@Getter
public enum Role {
    USER("ROLE_USER", "사용자"),
    ADMIN("ROLE_ADMIN", "관리자");

    private final String key;
    private final String value;

    Role(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
