package com.eunhasoo.bookclub.user.application;

import com.eunhasoo.bookclub.exception.user.DuplicatedNicknameException;
import com.eunhasoo.bookclub.exception.user.DuplicatedEmailException;
import com.eunhasoo.bookclub.exception.user.UserPasswordNotMatchException;
import com.eunhasoo.bookclub.exception.user.DuplicatedUsernameException;
import com.eunhasoo.bookclub.user.ui.request.UserCreate;
import com.eunhasoo.bookclub.user.ui.request.UserNicknameUpdate;
import com.eunhasoo.bookclub.user.domain.User;
import com.eunhasoo.bookclub.user.domain.UserRepository;
import com.eunhasoo.bookclub.user.ui.request.UserPasswordChange;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean isUsernameAlreadyUsed(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean isEmailAlreadyUsed(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional
    public void create(UserCreate userCreate) {
        checkIfUserIsDuplicated(userCreate.getUsername(), userCreate.getEmail());

        User user = User.builder()
                .username(userCreate.getUsername())
                .email(userCreate.getEmail())
                .password(passwordEncoder.encode(userCreate.getPassword()))
                .build();

        userRepository.save(user);
    }

    private void checkIfUserIsDuplicated(String username, String email) {
        if (isUsernameAlreadyUsed(username)) {
            throw new DuplicatedUsernameException(username);
        }

        if (isEmailAlreadyUsed(email)) {
            throw new DuplicatedEmailException(email);
        }
    }

    public User get(Long userId) {
        return userRepository.getById(userId);
    }

    @Transactional
    public void changeNickname(Long userId, UserNicknameUpdate userNicknameUpdate) {
        User user = userRepository.getById(userId);

        String nickname = userNicknameUpdate.getNickname();
        if (userRepository.existsByNickname(nickname)) {
            throw new DuplicatedNicknameException(nickname);
        }

        user.changeNickname(nickname);
    }

    @Transactional
    public void changePassword(Long userId, UserPasswordChange userPasswordChange) {
        User user = userRepository.getById(userId);

        String rawPassword = userPasswordChange.getCurPassword();
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new UserPasswordNotMatchException(userId);
        }

        user.changePassword(passwordEncoder.encode(userPasswordChange.getNewPassword()));
    }
}
