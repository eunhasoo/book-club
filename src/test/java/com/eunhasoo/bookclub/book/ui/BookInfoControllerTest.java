package com.eunhasoo.bookclub.book.ui;

import com.eunhasoo.bookclub.book.domain.BookInfo;
import com.eunhasoo.bookclub.book.domain.BookInfoRepository;
import com.eunhasoo.bookclub.book.ui.request.BookInfoCreate;
import com.eunhasoo.bookclub.book.ui.request.BookInfoSearch;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.eunhasoo.bookclub.helper.Fixture.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class BookInfoControllerTest {

    private static final String BOOK_INFO_API = "/api/bookInfo";

    @Autowired
    private BookInfoRepository bookInfoRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @AfterEach
    void clear() {
        bookInfoRepository.deleteAll();
    }

    @Test
    @DisplayName(BOOK_INFO_API + "/search GET 요청시 ISBN이 일치하는 책 정보가 있으면 책 정보 식별자를 응답으로 생성한다.")
    void find_exist() throws Exception {
        // given
        BookInfo bookInfo = bookInfo();
        bookInfoRepository.save(bookInfo);

        // expected
        BookInfoSearch bookInfoSearch = new BookInfoSearch(bookInfo.getIsbn());
        String json = om.writeValueAsString(bookInfoSearch);

        mockMvc.perform(get(BOOK_INFO_API + "/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isExist").value(true))
                .andExpect(jsonPath("$.bookInfoId").value(bookInfo.getId()))
                .andDo(print());
    }

    @Test
    @DisplayName(BOOK_INFO_API + "/search GET 요청시 ISBN이 일치하는 책 정보를 찾지 못하면 isExist 값을 false로 응답을 생성한다.")
    void find_not_exist() throws Exception {
        BookInfoSearch bookInfoSearch = new BookInfoSearch("9788960166332");

        mockMvc.perform(get(BOOK_INFO_API + "/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(bookInfoSearch)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isExist").value(false))
                .andDo(print());
    }

    @Test
    @DisplayName(BOOK_INFO_API + " GET 요청시 응답으로 책 정보를 생성한다.")
    void get_book_info_ok() throws Exception {
        BookInfo bookInfo = bookInfoRepository.save(bookInfo());

        mockMvc.perform(get(BOOK_INFO_API + "/{bookInfoId}", bookInfo.getId()))
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
    @DisplayName(BOOK_INFO_API + " GET 요청시 책 정보를 찾지 못하면 응답으로 404 코드를 생성한다.")
    void get_book_info_not_found() throws Exception {
        mockMvc.perform(get(BOOK_INFO_API + "/{bookInfo}", 1000L))
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
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(bookInfoCreate)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookInfoId").isNotEmpty())
                .andDo(print());
    }
}
