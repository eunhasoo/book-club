package com.eunhasoo.bookclub.book.application;

import com.eunhasoo.bookclub.book.domain.Book;
import com.eunhasoo.bookclub.book.domain.BookInfo;
import com.eunhasoo.bookclub.book.domain.BookInfoRepository;
import com.eunhasoo.bookclub.book.domain.BookRepository;
import com.eunhasoo.bookclub.book.domain.BookType;
import com.eunhasoo.bookclub.book.domain.Bookshelf;
import com.eunhasoo.bookclub.book.domain.BookshelfRepository;
import com.eunhasoo.bookclub.book.domain.Genre;
import com.eunhasoo.bookclub.book.domain.ReadProcess;
import com.eunhasoo.bookclub.book.ui.request.BookCreate;
import com.eunhasoo.bookclub.book.ui.request.BookSearch;
import com.eunhasoo.bookclub.book.ui.response.BookListResponse;
import com.eunhasoo.bookclub.exception.book.BookAlreadyExistException;
import com.eunhasoo.bookclub.exception.book.BookNotFoundException;
import com.eunhasoo.bookclub.exception.book.BookshelfAccessFailureException;
import com.eunhasoo.bookclub.helper.FixtureList;
import com.eunhasoo.bookclub.user.domain.User;
import com.eunhasoo.bookclub.user.domain.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.eunhasoo.bookclub.helper.Fixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@ActiveProfiles("test")
class BookServiceTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private BookInfoRepository bookInfoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookshelfRepository bookShelfRepository;

    @Autowired
    private BookRepository bookRepository;

    @AfterEach
    void clear() {
        bookRepository.deleteAll();
        bookShelfRepository.deleteAll();
        bookInfoRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("getAll 메소드는 책장의 책을 페이징하여 반환한다.")
    void get_all() {
        // given
        User user = userRepository.save(user());
        Bookshelf bookshelf = bookShelfRepository.save(bookshelf(user));
        List<BookInfo> bookInfoList = bookInfoRepository.saveAll(FixtureList.bookInfo(20, Genre.BIOGRAPHY, BookType.NON_FICTION));
        bookRepository.saveAll(FixtureList.book(bookshelf, user, bookInfoList));

        // when
        BookSearch bookSearch = new BookSearch(bookshelf.getId(), 1);
        List<Book> result = bookService.getAll(user.getId(), bookSearch);

        // then
        assertThat(result).hasSize(15);
    }

    @Test
    @DisplayName("getAll 메소드는 조회하려는 책장이 다른 회원의 소유이고 비공개 상태이면 예외를 던진다.")
    void get_all_access_fail() {
        // given
        User user = user();
        User anotherUser = User.builder()
                .username("another_user")
                .email("another@naver.com")
                .password("password")
                .build();

        userRepository.saveAll(List.of(user, anotherUser));

        Bookshelf bookshelf = Bookshelf.builder()
                .isOpen(false)
                .user(user)
                .name("20대 추천 서적 모음")
                .build();

        bookShelfRepository.save(bookshelf);


        // expected
        BookSearch bookSearch = new BookSearch(bookshelf.getId(), 1);
        assertThatThrownBy(() -> bookService.getAll(anotherUser.getId(), bookSearch))
                .isInstanceOf(BookshelfAccessFailureException.class);
    }

    @Transactional
    @Test
    @DisplayName("get 메소드는 회원의 책을 반환한다.")
    void get() {
        // given
        User user = userRepository.save(user());
        BookInfo bookInfo = bookInfoRepository.save(bookInfo());
        Bookshelf bookShelf = bookShelfRepository.save(bookshelf(user));
        Book book = bookRepository.save(book(bookShelf, user, bookInfo));

        // when
        Book found = bookService.get(user.getId(), book.getId());

        // then
        assertAll(
                () -> assertThat(found.getId()).isEqualTo(book.getId()),
                () -> assertThat(found.getBookInfo()).isEqualTo(book.getBookInfo()),
                () -> assertThat(found.getUser()).isEqualTo(book.getUser()),
                () -> assertThat(found.getBookshelf()).isEqualTo(book.getBookshelf())
        );
    }

    @Transactional
    @Test
    @DisplayName("get 메소드는 존재하지 않는 책 또는 회원의 식별자로 조회시 예외를 던진다.")
    void get_fail() {
        // given
        User user = userRepository.save(user());
        BookInfo bookInfo = bookInfoRepository.save(bookInfo());
        Bookshelf bookShelf = bookShelfRepository.save(bookshelf(user));
        Book book = bookRepository.save(book(bookShelf, user, bookInfo));

        // expected
        Long wrongBookId = book.getId() + 100L;
        Long wrongUserId = book.getUser().getId() + 100L;

        assertThatThrownBy(() -> bookService.get(book.getUser().getId(), wrongBookId))
                .isInstanceOf(BookNotFoundException.class);

        assertThatThrownBy(() -> bookService.get(book.getId(), wrongUserId))
                .isInstanceOf(BookNotFoundException.class);
    }

    @Test
    @DisplayName("create 메소드는 회원의 책장에 책을 생성한다.")
    @Transactional
    void create() {
        // given
        User user = userRepository.save(user());
        BookInfo bookInfo = bookInfoRepository.save(bookInfo());
        Bookshelf bookshelf = bookShelfRepository.save(bookshelf(user));

        // when
        BookCreate bookCreate = new BookCreate(bookInfo.getId(), bookshelf.getId(), ReadProcess.READING);
        bookService.create(user.getId(), bookCreate);

        // then
        List<Book> books = bookRepository.findAll();
        assertThat(books).hasSize(1);
    }

    @Test
    @DisplayName("create 메소드는 이미 책장에 존재하는 책을 생성하려고 하면 에러를 던진다.")
    @Transactional
    void create_fail() {
        // given
        User user = userRepository.save(user());
        BookInfo bookInfo = bookInfoRepository.save(bookInfo());
        Bookshelf bookshelf = bookShelfRepository.save(bookshelf(user));
        bookRepository.save(book(bookshelf, user, bookInfo));

        // expected
        BookCreate bookCreate = new BookCreate(bookInfo.getId(), bookshelf.getId(), ReadProcess.READING);

        assertThatThrownBy(() -> bookService.create(user.getId(), bookCreate))
                .isInstanceOf(BookAlreadyExistException.class);
    }

    @Test
    @DisplayName("delete 메소드는 회원의 책을 삭제한다.")
    void delete_success() {
        // given
        User user = userRepository.save(user());
        BookInfo bookInfo = bookInfoRepository.save(bookInfo());
        Bookshelf bookShelf = bookShelfRepository.save(bookshelf(user));
        Book book = bookRepository.save(book(bookShelf, user, bookInfo));

        List<Book> beforeDeleted = bookRepository.findAll();

        // when
        bookService.delete(book.getUser().getId(), book.getId());

        // then
        List<Book> afterDeleted = bookRepository.findAll();
        assertThat(beforeDeleted.size() - 1).isEqualTo(afterDeleted.size());
    }

    @Test
    @DisplayName("delete 메소드는 저장된 책을 찾지 못하면 에러를 던진다.")
    void delete_fail() {
        // given
        assertThatThrownBy(() -> bookService.get(1L, 1L))
                .isInstanceOf(BookNotFoundException.class);
    }
}
