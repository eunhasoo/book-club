package com.eunhasoo.bookclub.book.ui;

import com.eunhasoo.bookclub.auth.CustomUserDetails;
import com.eunhasoo.bookclub.auth.CustomUserDetailsService;
import com.eunhasoo.bookclub.auth.jwt.TokenProvider;
import com.eunhasoo.bookclub.book.domain.BookInfo;
import com.eunhasoo.bookclub.book.domain.BookInfoRepository;
import com.eunhasoo.bookclub.book.ui.request.BookInfoCreate;
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

import static com.eunhasoo.bookclub.helper.Fixture.*;
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
class BookInfoControllerTest {

    private static final String BOOK_INFO_API = "/api/bookinfo";

    @Autowired
    private BookInfoRepository bookInfoRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @MockBean
    private TokenProvider tokenProvider;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void clear() {
        bookInfoRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName(BOOK_INFO_API + "/search GET 요청시 ISBN이 일치하는 책 정보가 있으면 책 정보 식별자를 응답으로 생성한다.")
    void find_with_exist_isbn() throws Exception {
        // given
        BookInfo bookInfo = bookInfoRepository.save(bookInfo());
        User user = userRepository.save(Fixture.user());

        when(tokenProvider.validateToken(any())).thenReturn(true);
        when(tokenProvider.createToken(anyLong())).thenReturn("token");
        when(tokenProvider.getUserIdFromToken(any())).thenReturn(user.getId());
        when(userDetailsService.loadUserByUserId(any())).thenReturn(CustomUserDetails.create(user));

        // expected
        mockMvc.perform(get(BOOK_INFO_API + "/search")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                        .param("isbn", bookInfo.getIsbn()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isExist").value(true))
                .andExpect(jsonPath("$.bookInfoId").value(bookInfo.getId()))
                .andDo(print());
    }

    @Test
    @DisplayName(BOOK_INFO_API + "/search GET 요청시 ISBN이 일치하는 책 정보를 찾지 못하면 isExist 값을 false로 응답을 생성한다.")
    void find_with_not_exist_isbn() throws Exception {
        // given
        User user = userRepository.save(Fixture.user());
        String notExistISBN = "9999999999999";

        when(tokenProvider.validateToken(any())).thenReturn(true);
        when(tokenProvider.createToken(anyLong())).thenReturn("token");
        when(tokenProvider.getUserIdFromToken(any())).thenReturn(user.getId());
        when(userDetailsService.loadUserByUserId(any())).thenReturn(CustomUserDetails.create(user));

        // expected
        mockMvc.perform(get(BOOK_INFO_API + "/search")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                        .param("isbn", notExistISBN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isExist").value(false))
                .andDo(print());
    }

    @Test
    @DisplayName(BOOK_INFO_API + " GET 요청시 식별자로 조회한 책 정보를 응답으로 생성한다.")
    void get_book_info_with_id() throws Exception {
        // given
        BookInfo bookInfo = bookInfoRepository.save(bookInfo());
        User user = userRepository.save(Fixture.user());

        when(tokenProvider.validateToken(any())).thenReturn(true);
        when(tokenProvider.createToken(anyLong())).thenReturn("token");
        when(tokenProvider.getUserIdFromToken(any())).thenReturn(user.getId());
        when(userDetailsService.loadUserByUserId(any())).thenReturn(CustomUserDetails.create(user));

        // expected
        mockMvc.perform(get(BOOK_INFO_API + "/{bookInfoId}", bookInfo.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isOk())
                .andExpectAll(
                    jsonPath("$.id").value(bookInfo.getId()),
                    jsonPath("$.isbn").value(bookInfo.getIsbn()),
                    jsonPath("$.name").value(bookInfo.getName()),
                    jsonPath("$.author").value(bookInfo.getAuthor()),
                    jsonPath("$.genre").value(bookInfo.getGenre().name()),
                    jsonPath("$.bookType").value(bookInfo.getBookType().name()),
                    jsonPath("$.publisher").value(bookInfo.getPublisher()),
                    jsonPath("$.publishedDate").value(bookInfo.getPublishedDate().toString()),
                    jsonPath("$.imageUrl").value(bookInfo.getImageUrl())
                )
                .andDo(print());
    }

    @Test
    @DisplayName(BOOK_INFO_API + " GET 요청시 책 정보를 찾지 못하면 404 응답 코드와 메시지를 생성한다.")
    void get_book_info_not_found() throws Exception {
        User user = userRepository.save(Fixture.user());

        when(tokenProvider.validateToken(any())).thenReturn(true);
        when(tokenProvider.createToken(anyLong())).thenReturn("token");
        when(tokenProvider.getUserIdFromToken(any())).thenReturn(user.getId());
        when(userDetailsService.loadUserByUserId(any())).thenReturn(CustomUserDetails.create(user));

        mockMvc.perform(get(BOOK_INFO_API + "/{bookInfo}", 1000L)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isNotFound())
                .andExpectAll(
                        jsonPath("$.statusCode").value("404"),
                        jsonPath("$.message").value("책 정보를 찾을 수 없습니다. -> [bookInfoId: 1000]")
                )
                .andDo(print());
    }

    @Test
    @DisplayName(BOOK_INFO_API + " POST 요청시 저장한 책 정보의 식별자를 응답으로 생성한다.")
    void create_book_info() throws Exception {
        // given
        BookInfo bookInfo = bookInfo();
        User user = userRepository.save(Fixture.user());

        when(tokenProvider.validateToken(any())).thenReturn(true);
        when(tokenProvider.createToken(anyLong())).thenReturn("token");
        when(tokenProvider.getUserIdFromToken(any())).thenReturn(user.getId());
        when(userDetailsService.loadUserByUserId(any())).thenReturn(CustomUserDetails.create(user));

        BookInfoCreate bookInfoCreate = BookInfoCreate.builder()
                .name(bookInfo.getName())
                .isbn(bookInfo.getIsbn())
                .bookType(bookInfo.getBookType())
                .genre(bookInfo.getGenre())
                .author(bookInfo.getAuthor())
                .publisher(bookInfo.getPublisher())
                .publishedDate(bookInfo.getPublishedDate())
                .build();

        // expected
        mockMvc.perform(post(BOOK_INFO_API)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(bookInfoCreate)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookInfoId").isNotEmpty())
                .andDo(print());
    }
}
