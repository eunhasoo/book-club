package com.eunhasoo.bookclub.user.domain;

import com.eunhasoo.bookclub.exception.user.UserNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Optional<User> findByUsername(String username);

    default User getByUsername(String username) {
        return findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
    }

    default User getById(Long id) {
        return findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }
}
