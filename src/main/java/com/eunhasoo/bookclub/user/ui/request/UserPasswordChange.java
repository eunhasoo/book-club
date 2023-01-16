package com.eunhasoo.bookclub.user.ui.request;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
public class UserPasswordChange {

    @NotBlank(message = "현재 비밀번호를 입력해주세요.")
    private String curPassword;

    @NotBlank(message = "새로 변경할 비밀번호를 입력해주세요.")
    @Size(min = 8, message = "새로 변경할 비밀번호를 8자 이상 입력해주세요.")
    private String newPassword;

    private UserPasswordChange() {
    }

    public UserPasswordChange(String curPassword, String newPassword) {
        this.curPassword = curPassword;
        this.newPassword = newPassword;
    }
}
