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
import com.eunhasoo.bookclub.book.domain.ReadProcess;
import com.eunhasoo.bookclub.book.ui.request.BookSearch;
import com.eunhasoo.bookclub.book.ui.request.BookUpdate;
import com.eunhasoo.bookclub.helper.Fixture;
import com.eunhasoo.bookclub.helper.FixtureList;
import com.eunhasoo.bookclub.user.domain.User;
import com.eunhasoo.bookclub.user.domain.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
class BookControllerTest {

    private static final String BOOK_API = "/api/books";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookInfoRepository bookInfoRepository;

    @Autowired
    private BookshelfRepository bookShelfRepository;

    @MockBean
    private TokenProvider tokenProvider;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        when(tokenProvider.createToken(anyLong())).thenReturn("token");
        when(tokenProvider.validateToken(any())).thenReturn(true);
    }

    @AfterEach
    void clear() {
        bookRepository.deleteAll();
        bookShelfRepository.deleteAll();
        bookInfoRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName(BOOK_API + " GET 요청시 책장의 책을 조회하고 200 응답 코드를 생성한다")
    void get_all() throws Exception {
        // given
        User user = userRepository.save(user());
        Bookshelf bookShelf = bookShelfRepository.save(bookshelf(user));
        BookInfo bookInfo = bookInfoRepository.save(bookInfo());
        Book book = bookRepository.save(book(bookShelf, user, bookInfo));

        when(tokenProvider.getUserIdFromToken(any())).thenReturn(user.getId());
        when(userDetailsService.loadUserByUserId(any())).thenReturn(CustomUserDetails.create(user));

        // expected
        mockMvc.perform(get(BOOK_API)
                        .param("bookshelfId", String.valueOf(bookShelf.getId()))
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
    @DisplayName(BOOK_API + " GET 요청시 다른 회원의 비공개 책장이면 403 응답 코드를 생성한다")
    void get_all_access_forbidden() throws Exception {
        // given
        User user = userRepository.save(user());
        Bookshelf bookshelf = bookShelfRepository.save(
                Bookshelf.builder()
                        .isOpen(false)
                        .user(user)
                        .name("20대 추천 서적 모음")
                        .build()
        );

        User currentUser = userRepository.save(
                User.builder()
                    .username("another_user")
                    .email("another@naver.com")
                    .password("password")
                    .build()
        );

        when(tokenProvider.getUserIdFromToken(any())).thenReturn(currentUser.getId());
        when(userDetailsService.loadUserByUserId(any())).thenReturn(CustomUserDetails.create(currentUser));

        // expected
        mockMvc.perform(get(BOOK_API)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                        .param("bookshelfId", String.valueOf(bookshelf.getId()))
                        .param("page", "1"))
                .andExpect(status().isForbidden())
                .andExpectAll(
                        jsonPath("$.message").value("해당 책장은 비공개 상태이므로 접근할 수 없습니다."),
                        jsonPath("$.statusCode").value("403")
                )
                .andDo(print());
    }

    @Test
    @DisplayName(BOOK_API + " GET 요청시 다른 회원의 공개 책장이면 조회 데이터와 200 응답 코드를 생성한다")
    @Transactional
    void get_all_access_success() throws Exception {
        // given
        User user = userRepository.save(user());
        List<BookInfo> bookInfoList = bookInfoRepository.saveAll(FixtureList.bookInfo(5, Genre.BUSINESS, BookType.NON_FICTION));
        Bookshelf bookshelf = bookShelfRepository.save(bookshelf(user));
        bookRepository.saveAll(FixtureList.book(bookshelf, user, bookInfoList));

        User currentUser = userRepository.save(
                User.builder()
                    .username("another_user")
                    .email("another@naver.com")
                    .password("password")
                    .build()
        );

        when(tokenProvider.getUserIdFromToken(any())).thenReturn(currentUser.getId());
        when(userDetailsService.loadUserByUserId(any())).thenReturn(CustomUserDetails.create(currentUser));

        // expected
        mockMvc.perform(get(BOOK_API)
                        .param("bookshelfId", String.valueOf(bookshelf.getId()))
                        .param("page", "1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.count").value(5),
                        jsonPath("$.data").isNotEmpty()
                )
                .andDo(print());
    }

    @Test
    @DisplayName(BOOK_API + "/{bookId} POST 요청시 책의 읽기 상태를 수정하고 200 응답을 생성한다.")
    void update() throws Exception {
        // given
        User user = userRepository.save(user());
        Bookshelf bookShelf = bookShelfRepository.save(bookshelf(user));
        BookInfo bookInfo = bookInfoRepository.save(bookInfo());
        Book book = bookRepository.save(book(bookShelf, user, bookInfo));

        when(tokenProvider.getUserIdFromToken(any())).thenReturn(user.getId());
        when(userDetailsService.loadUserByUserId(any())).thenReturn(CustomUserDetails.create(user));

        // expected
        BookUpdate bookUpdate = new BookUpdate(ReadProcess.READ);

        mockMvc.perform(post(BOOK_API + "/{bookId}", book.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                        .content(om.writeValueAsString(bookUpdate))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName(BOOK_API + "/{bookId} DELETE 요청시 책을 삭제하고 204 응답 코드를 생성한다.")
    void delete_success() throws Exception {
        // given
        User user = userRepository.save(user());
        Bookshelf bookShelf = bookShelfRepository.save(bookshelf(user));
        BookInfo bookInfo = bookInfoRepository.save(bookInfo());
        Book book = bookRepository.save(book(bookShelf, user, bookInfo));

        when(tokenProvider.getUserIdFromToken(any())).thenReturn(user.getId());
        when(userDetailsService.loadUserByUserId(any())).thenReturn(CustomUserDetails.create(user));

        // expected
        BookUpdate bookUpdate = new BookUpdate(ReadProcess.READ);

        mockMvc.perform(delete(BOOK_API + "/{bookId}", book.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                        .content(om.writeValueAsString(bookUpdate))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print());
    }
}
