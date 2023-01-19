package com.eunhasoo.bookclub.user.application;

import com.eunhasoo.bookclub.exception.user.DuplicatedEmailException;
import com.eunhasoo.bookclub.exception.user.UserPasswordNotMatchException;
import com.eunhasoo.bookclub.exception.user.DuplicatedUsernameException;
import com.eunhasoo.bookclub.helper.Fixture;
import com.eunhasoo.bookclub.user.domain.User;
import com.eunhasoo.bookclub.user.domain.UserRepository;
import com.eunhasoo.bookclub.user.ui.request.UserCreate;
import com.eunhasoo.bookclub.user.ui.request.UserNicknameUpdate;
import com.eunhasoo.bookclub.user.ui.request.UserPasswordChange;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void clear() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("isUsernameAlreadyUsed 메소드는 회원 아이디가 이미 존재하면 true를 반환한다.")
    void check_exist_username() {
        // given
        User user = userRepository.save(Fixture.user());

        // when
        boolean result = userService.isUsernameAlreadyUsed(user.getUsername());

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isUsernameAlreadyUsed 메소드는 회원 아이디를 찾지 못하면 false를 반환한다.")
    void check_not_exist_username() {
        // when
        boolean result = userService.isUsernameAlreadyUsed("not_exist");

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("isEmailAlreadyUsed 메소드는 회원 이메일이 이미 존재하면 true를 반환한다.")
    void check_exist_email() {
        // given
        User user = userRepository.save(Fixture.user());

        // when
        boolean result = userService.isEmailAlreadyUsed(user.getEmail());

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isEmailAlreadyUsed 메소드는 회원 이메일을 찾을 수 없으면 false를 반환한다.")
    void check_not_exist_email() {
        // when
        boolean result = userService.isEmailAlreadyUsed("non_exist@naver.com");

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("create 메소드는 비밀번호를 인코딩하고 닉네임을 랜덤하게 설정하여 회원을 저장한다.")
    void create_user_success() {
        // given
        User user = Fixture.user();
        UserCreate userCreate = new UserCreate(user.getUsername(), user.getEmail(), user.getPassword());

        // when
        userService.create(userCreate);

        // then
        User savedUser = userRepository.getByUsername(user.getUsername());

        assertThat(savedUser.getNickname()).isNotEmpty();
        assertThat(savedUser.getPassword()).isNotEqualTo(user.getPassword());
        assertThat(savedUser.getUsername()).isEqualTo(user.getUsername());
        assertThat(savedUser.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    @DisplayName("create 메소드는 이미 존재하는 회원 아이디나 이메일으로 저장을 시도하면 에러를 던진다.")
    void create_user_fail() {
        // given
        User user = userRepository.save(Fixture.user());
        String existUsername = user.getUsername();
        String existEmail = user.getEmail();

        // expected
        UserCreate duplicatedUsername = new UserCreate(existUsername, "new" + existEmail, user.getPassword());
        UserCreate duplicatedEmail = new UserCreate("new" + existUsername, existEmail, user.getPassword());

        assertThatThrownBy(() -> userService.create(duplicatedUsername))
                .isInstanceOf(DuplicatedUsernameException.class);

        assertThatThrownBy(() -> userService.create(duplicatedEmail))
                .isInstanceOf(DuplicatedEmailException.class);
    }

    @Test
    @DisplayName("get 메소드는 회원의 정보를 조회한다.")
    void get() {
        // given
        User user = userRepository.save(Fixture.userWithEncodedPassword());

        // when
        User found = userService.get(user.getId());

        // then
        assertThat(found.getId()).isEqualTo(user.getId());
        assertThat(found.getUsername()).isEqualTo(user.getUsername());
        assertThat(found.getEmail()).isEqualTo(user.getEmail());
        assertThat(found.getNickname()).isEqualTo(user.getNickname());
    }

    @Test
    @DisplayName("changeNickname 메소드는 회원의 닉네임을 변경한다.")
    void change_nickname() {
        // given
        User beforeUpdate = userRepository.save(Fixture.user());
        String prevNickname = beforeUpdate.getNickname();

        // when
        UserNicknameUpdate userNicknameUpdate = new UserNicknameUpdate("다독이");
        userService.changeNickname(beforeUpdate.getId(), userNicknameUpdate);

        // then
        User afterUpdate = userRepository.getById(beforeUpdate.getId());
        assertThat(prevNickname).isNotEqualTo(afterUpdate.getNickname());
        assertThat(afterUpdate.getNickname()).isEqualTo(userNicknameUpdate.getNickname());
    }

    @Test
    @DisplayName("changePassword 메소드는 회원의 비밀번호를 변경한다.")
    void change_password_success() {
        // given
        User beforeUpdate = userRepository.save(Fixture.userWithEncodedPassword());

        // when
        String newPassword = "hello12345";
        userService.changePassword(beforeUpdate.getId(), new UserPasswordChange(Fixture.user().getPassword(), newPassword));

        // then
        User afterUpdate = userRepository.getById(beforeUpdate.getId());
        assertThat(beforeUpdate.getPassword()).isNotEqualTo(afterUpdate.getPassword());
    }

    @Test
    @DisplayName("updatePassword 메소드는 비밀번호가 일치하지 않으면 에러를 던진다.")
    void update_password_fail() {
        // given
        User user = userRepository.save(Fixture.userWithEncodedPassword());

        // expected
        String wrongPassword = Fixture.user().getPassword() + "12345";
        String newPassword = "hello12345";

        UserPasswordChange userPasswordChange = new UserPasswordChange(wrongPassword, newPassword);

        assertThatThrownBy(() -> userService.changePassword(user.getId(), userPasswordChange))
                .isInstanceOf(UserPasswordNotMatchException.class);
    }
}
