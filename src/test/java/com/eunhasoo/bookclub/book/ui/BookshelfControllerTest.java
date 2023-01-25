package com.eunhasoo.bookclub.book.ui;

import com.eunhasoo.bookclub.auth.CustomUserDetails;
import com.eunhasoo.bookclub.auth.CustomUserDetailsService;
import com.eunhasoo.bookclub.auth.jwt.TokenProvider;
import com.eunhasoo.bookclub.book.domain.BookInfoRepository;
import com.eunhasoo.bookclub.book.domain.Bookshelf;
import com.eunhasoo.bookclub.book.domain.BookshelfRepository;
import com.eunhasoo.bookclub.book.ui.request.BookshelfCreate;
import com.eunhasoo.bookclub.book.ui.request.BookshelfUpdate;
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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
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
class BookshelfControllerTest {

    private static final String BOOKSHELF_API = "/api/bookshelves";

    @Autowired
    private BookshelfRepository bookshelfRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @MockBean
    private TokenProvider tokenProvider;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    @AfterEach
    void clear() {
        bookshelfRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName(BOOKSHELF_API + " POST 요청시 책장을 새로 저장하고 201 응답 코드를 생성한다.")
    @Transactional
    void create() throws Exception {
        // given
        User user = userRepository.save(Fixture.user());

        // when
        when(tokenProvider.validateToken(any())).thenReturn(true);
        when(tokenProvider.createToken(anyLong())).thenReturn("token");
        when(tokenProvider.getUserIdFromToken(any())).thenReturn(user.getId());
        when(userDetailsService.loadUserByUserId(any())).thenReturn(CustomUserDetails.create(user));

        // then
        BookshelfCreate bookshelfCreate = new BookshelfCreate("2023 읽을 책들", true);

        mockMvc.perform(post(BOOKSHELF_API)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                        .content(om.writeValueAsString(bookshelfCreate))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName(BOOKSHELF_API + " GET 요청시 회원의 모든 책장을 조회하고 200 응답 코드를 생성한다.")
    @Transactional
    void get_all_bookshelves() throws Exception {
        // given
        User user = userRepository.save(Fixture.user());
        Bookshelf bookshelf = bookshelfRepository.save(Fixture.bookshelf(user));

        // when
        when(tokenProvider.validateToken(any())).thenReturn(true);
        when(tokenProvider.createToken(anyLong())).thenReturn("token");
        when(tokenProvider.getUserIdFromToken(any())).thenReturn(user.getId());
        when(userDetailsService.loadUserByUserId(any())).thenReturn(CustomUserDetails.create(user));

        // then
        mockMvc.perform(get(BOOKSHELF_API)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.count").value(1),
                        jsonPath("$.data[0].id").value(bookshelf.getId()),
                        jsonPath("$.data[0].name").value(bookshelf.getName())
                )
                .andDo(print());
    }

    @Test
    @DisplayName(BOOKSHELF_API + "/{bookshelfId} POST 요청시 책장을 수정하고 200 응답 코드를 생성한다.")
    void update_success() throws Exception {
        // given
        User user = userRepository.save(Fixture.user());
        Bookshelf bookshelf = bookshelfRepository.save(Fixture.bookshelf(user));

        // when
        when(tokenProvider.validateToken(any())).thenReturn(true);
        when(tokenProvider.createToken(anyLong())).thenReturn("token");
        when(tokenProvider.getUserIdFromToken(any())).thenReturn(user.getId());
        when(userDetailsService.loadUserByUserId(any())).thenReturn(CustomUserDetails.create(user));

        BookshelfUpdate bookshelfUpdate = new BookshelfUpdate("추리 소설 모음", false);

        // then
        mockMvc.perform(post(BOOKSHELF_API + "/{bookshelfId}", bookshelf.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(bookshelfUpdate))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName(BOOKSHELF_API + "/{bookshelfId} DELETE 요청시 책장을 삭제하고 204 응답 코드를 생성한다.")
    void delete_success() throws Exception {
        // given
        User user = userRepository.save(Fixture.user());
        Bookshelf bookshelf = bookshelfRepository.save(Fixture.bookshelf(user));

        // when
        when(tokenProvider.validateToken(any())).thenReturn(true);
        when(tokenProvider.createToken(anyLong())).thenReturn("token");
        when(tokenProvider.getUserIdFromToken(any())).thenReturn(user.getId());
        when(userDetailsService.loadUserByUserId(any())).thenReturn(CustomUserDetails.create(user));

        // then
        mockMvc.perform(delete(BOOKSHELF_API + "/{bookshelfId}", bookshelf.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isNoContent())
                .andDo(print());
    }
}
