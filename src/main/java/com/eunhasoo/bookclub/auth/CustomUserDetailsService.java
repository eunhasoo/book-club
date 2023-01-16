package com.eunhasoo.bookclub.auth;

import com.eunhasoo.bookclub.exception.user.UserNotFoundException;
import com.eunhasoo.bookclub.user.domain.User;
import com.eunhasoo.bookclub.user.domain.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.getByUsername(username);
        return CustomUserDetails.create(user);
    }

    public UserDetails loadUserByUserId(Long userId) {
        return userRepository.findById(userId)
                .map(user -> CustomUserDetails.create(user))
                .orElseThrow(() -> new UserNotFoundException(userId));
    }
}
