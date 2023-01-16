package com.eunhasoo.bookclub.user.ui;

import com.eunhasoo.bookclub.auth.CurrentUser;
import com.eunhasoo.bookclub.user.ui.request.CheckUserResponse;
import com.eunhasoo.bookclub.user.ui.request.UserCreate;
import com.eunhasoo.bookclub.user.application.UserService;
import com.eunhasoo.bookclub.user.domain.User;
import com.eunhasoo.bookclub.user.ui.request.UserNicknameUpdate;
import com.eunhasoo.bookclub.user.ui.request.UserPasswordChange;
import com.eunhasoo.bookclub.user.ui.response.UserInfoResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequestMapping("/api/users")
@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/id")
    public CheckUserResponse checkUsername(@RequestParam String id) {
        return new CheckUserResponse(userService.isUsernameAlreadyUsed(id));
    }

    @GetMapping("/email")
    public CheckUserResponse checkEmail(@RequestParam String email) {
        return new CheckUserResponse(userService.isEmailAlreadyUsed(email));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void signUp(@RequestBody @Valid UserCreate userCreate) {
        userService.create(userCreate);
    }

    @GetMapping("/me")
    public UserInfoResponse getUserInfo(@CurrentUser Long userId) {
        User user = userService.get(userId);
        return new UserInfoResponse(user);
    }

    @PostMapping("/nickname")
    public void changeNickname(@CurrentUser Long userId,
                               @RequestBody @Valid UserNicknameUpdate userNicknameUpdate) {
        userService.changeNickname(userId, userNicknameUpdate);
    }

    @PostMapping("/password")
    public void updatePassword(@CurrentUser Long userId,
                               @RequestBody @Valid UserPasswordChange userPasswordChange) {
        userService.changePassword(userId, userPasswordChange);
    }
}
