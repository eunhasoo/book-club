package com.eunhasoo.bookclub.book.ui;

import com.eunhasoo.bookclub.auth.CustomUserDetails;
import com.eunhasoo.bookclub.auth.CustomUserDetailsService;
import com.eunhasoo.bookclub.auth.jwt.TokenProvider;
import com.eunhasoo.bookclub.book.domain.Book;
import com.eunhasoo.bookclub.book.domain.BookInfo;
import com.eunhasoo.bookclub.book.domain.BookInfoRepository;
import com.eunhasoo.bookclub.book.domain.BookRepository;
import com.eunhasoo.bookclub.book.domain.Bookshelf;
import com.eunhasoo.bookclub.book.domain.BookshelfRepository;
import com.eunhasoo.bookclub.book.domain.ReadProcess;
import com.eunhasoo.bookclub.book.ui.request.BookUpdate;
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

import static com.eunhasoo.bookclub.helper.Fixture.book;
import static com.eunhasoo.bookclub.helper.Fixture.bookInfo;
import static com.eunhasoo.bookclub.helper.Fixture.bookshelf;
import static com.eunhasoo.bookclub.helper.Fixture.user;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
