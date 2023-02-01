package com.eunhasoo.bookclub.auth;

import com.eunhasoo.bookclub.auth.jwt.TokenProvider;
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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private TokenProvider tokenProvider;

    @AfterEach
    void clear() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("로그인 성공시 생성한 JWT 문자열을 응답과 Authorization 헤더로 생성한다.")
    void login_success() throws Exception {
        //given
        User user = userRepository.save(Fixture.userWithEncodedPassword());
        LoginRequest loginRequest = new LoginRequest(user.getUsername(), Fixture.user().getPassword());

        when(tokenProvider.createToken(anyLong())).thenReturn("token");

        // then
        mockMvc.perform(post("/api/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(jsonPath("$.token").value("token"))
                .andDo(print());
    }
}
