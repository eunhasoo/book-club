package com.eunhasoo.bookclub.book.ui;

import com.eunhasoo.bookclub.auth.CustomUserDetails;
import com.eunhasoo.bookclub.auth.CustomUserDetailsService;
import com.eunhasoo.bookclub.auth.jwt.TokenProvider;
import com.eunhasoo.bookclub.book.domain.Book;
import com.eunhasoo.bookclub.book.domain.BookInfo;
import com.eunhasoo.bookclub.book.domain.BookInfoRepository;
import com.eunhasoo.bookclub.book.domain.BookRepository;
import com.eunhasoo.bookclub.book.domain.BookType;
import com.eunhasoo.bookclub.book.domain.Bookshelf;
import com.eunhasoo.bookclub.book.domain.BookshelfRepository;
import com.eunhasoo.bookclub.book.domain.Genre;
import com.eunhasoo.bookclub.book.ui.request.BookshelfCreate;
import com.eunhasoo.bookclub.book.ui.request.BookshelfUpdate;
import com.eunhasoo.bookclub.helper.FixtureList;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.eunhasoo.bookclub.helper.Fixture.book;
import static com.eunhasoo.bookclub.helper.Fixture.bookInfo;
import static com.eunhasoo.bookclub.helper.Fixture.bookshelf;
import static com.eunhasoo.bookclub.helper.Fixture.user;
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
    private BookInfoRepository bookInfoRepository;

    @Autowired
    private BookRepository bookRepository;

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
        bookRepository.deleteAll();
        bookshelfRepository.deleteAll();
        bookInfoRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName(BOOKSHELF_API + " POST ????????? ????????? ?????? ???????????? 201 ?????? ????????? ????????????.")
    @Transactional
    void create() throws Exception {
        // given
        User user = userRepository.save(user());

        // when
        when(tokenProvider.validateToken(any())).thenReturn(true);
        when(tokenProvider.createToken(anyLong())).thenReturn("token");
        when(tokenProvider.getUserIdFromToken(any())).thenReturn(user.getId());
        when(userDetailsService.loadUserByUserId(any())).thenReturn(CustomUserDetails.create(user));

        // then
        BookshelfCreate bookshelfCreate = new BookshelfCreate("2023 ?????? ??????", true);

        mockMvc.perform(post(BOOKSHELF_API)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                        .content(om.writeValueAsString(bookshelfCreate))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName(BOOKSHELF_API + " GET ????????? ????????? ?????? ????????? ???????????? 200 ?????? ????????? ????????????.")
    @Transactional
    void get_all_bookshelves() throws Exception {
        // given
        User user = userRepository.save(user());
        Bookshelf bookshelf = bookshelfRepository.save(bookshelf(user));

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
    @DisplayName(BOOKSHELF_API + "/{bookshelfId} POST ????????? ????????? ???????????? 200 ?????? ????????? ????????????.")
    void update_success() throws Exception {
        // given
        User user = userRepository.save(user());
        Bookshelf bookshelf = bookshelfRepository.save(bookshelf(user));

        // when
        when(tokenProvider.validateToken(any())).thenReturn(true);
        when(tokenProvider.createToken(anyLong())).thenReturn("token");
        when(tokenProvider.getUserIdFromToken(any())).thenReturn(user.getId());
        when(userDetailsService.loadUserByUserId(any())).thenReturn(CustomUserDetails.create(user));

        BookshelfUpdate bookshelfUpdate = new BookshelfUpdate("?????? ?????? ??????", false);

        // then
        mockMvc.perform(post(BOOKSHELF_API + "/{bookshelfId}", bookshelf.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(bookshelfUpdate))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName(BOOKSHELF_API + "/{bookshelfId} DELETE ????????? ????????? ???????????? 204 ?????? ????????? ????????????.")
    void delete_success() throws Exception {
        // given
        User user = userRepository.save(user());
        Bookshelf bookshelf = bookshelfRepository.save(bookshelf(user));

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


    @Test
    @DisplayName(BOOKSHELF_API + "/{bookshelfId}/books GET ????????? ????????? ?????? ???????????? 200 ?????? ????????? ????????????")
    void get_all() throws Exception {
        // given
        User user = userRepository.save(user());
        Bookshelf bookshelf = bookshelfRepository.save(bookshelf(user));
        BookInfo bookInfo = bookInfoRepository.save(bookInfo());
        Book book = bookRepository.save(book(bookshelf, user, bookInfo));

        when(tokenProvider.validateToken(any())).thenReturn(true);
        when(tokenProvider.createToken(anyLong())).thenReturn("token");
        when(tokenProvider.getUserIdFromToken(any())).thenReturn(user.getId());
        when(userDetailsService.loadUserByUserId(any())).thenReturn(CustomUserDetails.create(user));

        // expected
        mockMvc.perform(get(BOOKSHELF_API + "/{bookshelfId}/books", bookshelf.getId())
                        .param("page", "1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.count").value(1),
                        jsonPath("$.data[0].id").value(book.getId()),
                        jsonPath("$.data[0].bookInfoId").value(bookInfo.getId()),
                        jsonPath("$.data[0].name").value(bookInfo.getName()),
                        jsonPath("$.data[0].author").value(bookInfo.getAuthor()),
                        jsonPath("$.data[0].imageUrl").value(bookInfo.getImageUrl())
                )
                .andDo(print());
    }

    @Test
    @DisplayName(BOOKSHELF_API + "/{bookshelfId}/books GET ????????? ?????? ????????? ????????? ???????????? 403 ?????? ????????? ????????????")
    void get_all_access_forbidden() throws Exception {
        // given
        User user = userRepository.save(user());
        Bookshelf bookshelf = bookshelfRepository.save(
                Bookshelf.builder()
                        .isOpen(false)
                        .user(user)
                        .name("20??? ?????? ?????? ??????")
                        .build()
        );

        User currentUser = userRepository.save(
                User.builder()
                        .username("another_user")
                        .email("another@naver.com")
                        .password("password")
                        .build()
        );

        when(tokenProvider.validateToken(any())).thenReturn(true);
        when(tokenProvider.createToken(anyLong())).thenReturn("token");
        when(tokenProvider.getUserIdFromToken(any())).thenReturn(currentUser.getId());
        when(userDetailsService.loadUserByUserId(any())).thenReturn(CustomUserDetails.create(currentUser));

        // expected
        mockMvc.perform(get(BOOKSHELF_API + "/{bookshelfId}/books", bookshelf.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                        .param("page", "1"))
                .andExpect(status().isForbidden())
                .andExpectAll(
                        jsonPath("$.message").value("?????? ????????? ????????? ??????????????? ????????? ??? ????????????."),
                        jsonPath("$.statusCode").value("403")
                )
                .andDo(print());
    }

    @Test
    @DisplayName(BOOKSHELF_API + "/{bookshelfId}/books GET ????????? ?????? ????????? ?????? ???????????? ?????? ???????????? 200 ?????? ????????? ????????????")
    @Transactional
    void get_all_access_success() throws Exception {
        // given
        User user = userRepository.save(user());
        List<BookInfo> bookInfoList = bookInfoRepository.saveAll(FixtureList.bookInfo(20, Genre.BUSINESS, BookType.NON_FICTION));
        Bookshelf bookshelf = bookshelfRepository.save(bookshelf(user));
        bookRepository.saveAll(FixtureList.book(bookshelf, user, bookInfoList));

        User currentUser = userRepository.save(
                User.builder()
                        .username("another_user")
                        .email("another@naver.com")
                        .password("password")
                        .build()
        );

        when(tokenProvider.validateToken(any())).thenReturn(true);
        when(tokenProvider.createToken(anyLong())).thenReturn("token");
        when(tokenProvider.getUserIdFromToken(any())).thenReturn(currentUser.getId());
        when(userDetailsService.loadUserByUserId(any())).thenReturn(CustomUserDetails.create(currentUser));

        // expected
        mockMvc.perform(get(BOOKSHELF_API + "/{bookshelfId}/books", bookshelf.getId())
                        .param("page", "1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.count").value(15),
                        jsonPath("$.data").isNotEmpty()
                )
                .andDo(print());
    }
}
