package com.eunhasoo.bookclub.user.ui.request;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
public class UserNicknameUpdate {

    @NotBlank(message = "닉네임을 입력해주세요.")
    @Size(min = 3, message = "닉네임을 3자 이상 입력해주세요.")
    @Size(max = 18, message = "닉네임을 18자 이내로 입력해주세요.")
    private String nickname;

    private UserNicknameUpdate() {
    }

    public UserNicknameUpdate(String nickname) {
        this.nickname = nickname;
    }
}
