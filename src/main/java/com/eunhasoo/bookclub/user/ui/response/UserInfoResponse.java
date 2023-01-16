package com.eunhasoo.bookclub.user.ui.response;

import com.eunhasoo.bookclub.user.domain.User;
import lombok.Getter;

@Getter
public class UserInfoResponse {
    private Long id;
    private String email;
    private String username;
    private String nickname;

    public UserInfoResponse(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.nickname = user.getNickname();
    }
}
