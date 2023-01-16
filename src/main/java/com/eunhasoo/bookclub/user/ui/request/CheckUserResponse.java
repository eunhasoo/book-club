package com.eunhasoo.bookclub.user.ui.request;

import lombok.Getter;

@Getter
public class CheckUserResponse {

    private String using;

    public CheckUserResponse(boolean using) {
        this.using = using ? "Y" : "N";
    }
}
