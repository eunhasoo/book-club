package com.eunhasoo.bookclub.auth;

import com.eunhasoo.bookclub.auth.ui.LoginRequest;
import com.eunhasoo.bookclub.helper.Fixture;
import com.eunhasoo.bookclub.user.domain.User;
import com.eunhasoo.bookclub.user.domain.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
class AuthenticationFailureHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper om;

    @AfterEach
    void clear() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("로그인 요청시 비밀번호가 틀리면 401 에러 코드와 메세지를 응답으로 반환한다.")
    void login_fail_with_wrong_password() throws Exception {
        // given
        User user = userRepository.save(Fixture.userWithEncodedPassword());

        // expected
        String wrongPassword = "ABC123456789";
        LoginRequest loginRequest = new LoginRequest(user.getUsername(), wrongPassword);

        mockMvc.perform(post("/api/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.statusCode").value("401"))
                .andExpect(jsonPath("$.message").value("입력한 아이디 혹은 비밀번호가 올바르지 않습니다."));
    }

    @Test
    @DisplayName("로그인 요청시 아이디를 찾을 수 없으면 404 응답 코드와 오류 메세지를 생성한다.")
    void login_fail_with_wrong_username() throws Exception {
        // given
        User user = userRepository.save(Fixture.userWithEncodedPassword());

        // expected
        String wrongUsername = user.getUsername() + "wrong";
        LoginRequest loginRequest = new LoginRequest(wrongUsername, Fixture.user().getPassword());

        mockMvc.perform(post("/api/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(loginRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value("404"))
                .andExpect(jsonPath("$.message").value(String.format("회원 정보를 찾을 수 없습니다. -> [username: %s]", wrongUsername)));
    }
}
