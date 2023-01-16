package com.eunhasoo.bookclub.user.ui.request;

import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
public class UserCreate {

    @NotBlank(message = "아이디를 입력해주세요.")
    @Size(min = 4, message = "아이디를 4자 이상 입력해주세요.")
    @Size(max = 18, message = "아이디를 18자 이하로 입력해주세요.")
    private String username;

    @Email(message = "이메일을 형식에 맞게 입력해주세요.")
    private String email;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(min = 8, message = "비밀번호를 8자 이상 입력해주세요.")
    private String password;

    private UserCreate() {
    }

    public UserCreate(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
}
