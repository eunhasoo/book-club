package com.eunhasoo.bookclub.book.application;

import com.eunhasoo.bookclub.book.domain.BookInfo;
import com.eunhasoo.bookclub.book.domain.BookInfoRepository;
import com.eunhasoo.bookclub.book.domain.BookType;
import com.eunhasoo.bookclub.book.domain.Genre;
import com.eunhasoo.bookclub.book.ui.request.BookInfoCreate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static com.eunhasoo.bookclub.helper.Fixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@ActiveProfiles("test")
class BookInfoServiceTest {

    @Autowired
    private BookInfoService bookInfoService;

    @Autowired
    private BookInfoRepository bookInfoRepository;

    @AfterEach
    void clear() {
        bookInfoRepository.deleteAll();
    }

    @Test
    @DisplayName("get 메소드는 ID로 조회한 책 정보를 반환한다.")
    void get_book_info() {
        // given
        BookInfo bookInfo = bookInfo();
        bookInfoRepository.save(bookInfo);

        // when
        BookInfo found = bookInfoService.get(bookInfo.getId());

        // then
        assertAll(
                () -> assertThat(found.getName()).isEqualTo(bookInfo.getName()),
                () -> assertThat(found.getIsbn()).isEqualTo(bookInfo.getIsbn()),
                () -> assertThat(found.getAuthor()).isEqualTo(bookInfo.getAuthor()),
                () -> assertThat(found.getGenre()).isEqualTo(bookInfo.getGenre()),
                () -> assertThat(found.getBookType()).isEqualTo(bookInfo.getBookType()),
                () -> assertThat(found.getPublisher()).isEqualTo(bookInfo.getPublisher()),
                () -> assertThat(found.getPublishedDate()).isEqualTo(bookInfo.getPublishedDate())
        );
    }

    @Test
    @DisplayName("getIdByIsbn 메소드는 ISBN으로 조회 가능하면 책 정보의 식별자를 반환한다.")
    void get_with_exist_isbn() {
        // given
        BookInfo bookInfo = bookInfo();
        bookInfoRepository.save(bookInfo);

        // when
        Long id = bookInfoService.getIdByIsbn(bookInfo.getIsbn());

        // then
        assertThat(bookInfo.getId()).isEqualTo(id);
    }

    @Test
    @DisplayName("getIdByIsbn 메소드는 ISBN으로 찾을 수 없으면 null을 반환한다.")
    void get_with_not_exist_isbn() {
        // when
        Long id = bookInfoService.getIdByIsbn("9788960166332");

        // then
        assertThat(id).isNull();
    }

    @Test
    @DisplayName("create 메소드는 책 정보를 저장하고 식별자를 반환한다.")
    void create() {
        // given
        LocalDate now = LocalDate.now();
        BookInfoCreate bookInfoCreate = BookInfoCreate.builder()
                .name("잭과 콩나무")
                .author("영국민화")
                .bookType(BookType.FICTION)
                .genre(Genre.FANTASY)
                .isbn("9788960166332")
                .publisher("삼성출판사")
                .publishedDate(now)
                .build();

        // when
        Long bookInfoId = bookInfoService.create(bookInfoCreate);

        // then
        BookInfo bookInfo = bookInfoRepository.getById(bookInfoId);
        assertAll(
                () -> assertThat(bookInfo.getId()).isEqualTo(bookInfoId),
                () -> assertThat(bookInfo.getName()).isEqualTo("잭과 콩나무"),
                () -> assertThat(bookInfo.getAuthor()).isEqualTo("영국민화"),
                () -> assertThat(bookInfo.getPublisher()).isEqualTo("삼성출판사"),
                () -> assertThat(bookInfo.getIsbn()).isEqualTo("9788960166332"),
                () -> assertThat(bookInfo.getBookType()).isEqualTo(BookType.FICTION),
                () -> assertThat(bookInfo.getGenre()).isEqualTo(Genre.FANTASY),
                () -> assertThat(bookInfo.getPublishedDate()).isEqualTo(now.toString())
        );
    }
}
