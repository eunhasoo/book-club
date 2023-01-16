package com.eunhasoo.bookclub.user.ui;

import com.eunhasoo.bookclub.auth.CustomUserDetails;
import com.eunhasoo.bookclub.auth.CustomUserDetailsService;
import com.eunhasoo.bookclub.auth.jwt.TokenProvider;
import com.eunhasoo.bookclub.helper.Fixture;
import com.eunhasoo.bookclub.user.domain.User;
import com.eunhasoo.bookclub.user.domain.UserRepository;
import com.eunhasoo.bookclub.user.ui.request.UserCreate;
import com.eunhasoo.bookclub.user.ui.request.UserNicknameUpdate;
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
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerTest {

    private static final String USER_API = "/api/users";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private TokenProvider tokenProvider;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    @AfterEach
    void clear() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName(USER_API + "/id 요청시 회원 아이디가 존재하면 \"Y\"를 응답으로 생성한다.")
    void check_username() throws Exception {
        // given
        User user = userRepository.save(Fixture.user());

        // expected
        mockMvc.perform(get(USER_API + "/id")
                        .param("id", user.getUsername()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.using").value("Y"))
                .andDo(print());
    }

    @Test
    @DisplayName(USER_API + "/email 요청시 회원 이메일이 존재하면 \"Y\"를 응답으로 생성한다.")
    void check_email() throws Exception {
        // given
        User user = userRepository.save(Fixture.user());

        // expected
        mockMvc.perform(get(USER_API + "/email")
                        .param("email", user.getEmail()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.using").value("Y"))
                .andDo(print());
    }

    @Test
    @DisplayName(USER_API + " POST 요청시 회원을 생성하고 201 응답 코드를 생성한다.")
    @Transactional
    void sign_up() throws Exception {
        // given
        User user = Fixture.user();

        // expected
        UserCreate userCreate = new UserCreate(user.getUsername(), user.getEmail(), user.getPassword());

        mockMvc.perform(post(USER_API)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(userCreate)))
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @Test
    @DisplayName(USER_API + "/nickname 요청시 회원의 기존 닉네임을 변경한다.")
    void update_nickname_success() throws Exception {
        // given
        User user = userRepository.save(Fixture.user());

        when(tokenProvider.validateToken(any())).thenReturn(true);
        when(tokenProvider.createToken(anyLong())).thenReturn("token");
        when(userDetailsService.loadUserByUserId(any())).thenReturn(CustomUserDetails.create(user));
        when(tokenProvider.getUserIdFromToken(any())).thenReturn(user.getId());

        // expected
        UserNicknameUpdate userNicknameUpdate = new UserNicknameUpdate("다독이");

        mockMvc.perform(post(USER_API + "/nickname")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(userNicknameUpdate))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName(USER_API + "/nickname 요청시 회원의 기존 닉네임을 변경한다.")
    void change_password_success() throws Exception {
        // given
        User user = userRepository.save(Fixture.user());

        when(tokenProvider.validateToken(any())).thenReturn(true);
        when(tokenProvider.createToken(anyLong())).thenReturn("token");
        when(userDetailsService.loadUserByUserId(any())).thenReturn(CustomUserDetails.create(user));
        when(tokenProvider.getUserIdFromToken(any())).thenReturn(user.getId());

        // expected
        UserNicknameUpdate userNicknameUpdate = new UserNicknameUpdate("다독이");

        mockMvc.perform(post(USER_API + "/nickname")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(userNicknameUpdate))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isOk())
                .andDo(print());
    }
}
