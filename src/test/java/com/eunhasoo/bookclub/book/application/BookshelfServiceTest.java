package com.eunhasoo.bookclub.book.application;

import com.eunhasoo.bookclub.book.domain.Book;
import com.eunhasoo.bookclub.book.domain.BookInfo;
import com.eunhasoo.bookclub.book.domain.BookInfoRepository;
import com.eunhasoo.bookclub.book.domain.BookRepository;
import com.eunhasoo.bookclub.book.domain.BookType;
import com.eunhasoo.bookclub.book.domain.Bookshelf;
import com.eunhasoo.bookclub.book.domain.BookshelfCreationPolicy;
import com.eunhasoo.bookclub.book.domain.BookshelfRepository;
import com.eunhasoo.bookclub.book.domain.Genre;
import com.eunhasoo.bookclub.book.ui.request.BookshelfCreate;
import com.eunhasoo.bookclub.book.ui.request.BookshelfUpdate;
import com.eunhasoo.bookclub.exception.book.BookNotFoundException;
import com.eunhasoo.bookclub.exception.book.InvalidBookshelfCreationPolicy;
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
import java.util.stream.IntStream;

import static com.eunhasoo.bookclub.helper.Fixture.*;
import static com.eunhasoo.bookclub.helper.FixtureList.book;
import static com.eunhasoo.bookclub.helper.FixtureList.bookInfo;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class BookshelfServiceTest {

    @Autowired
    private BookshelfService bookShelfService;

    @Autowired
    private BookshelfRepository bookshelfRepository;

    @Autowired
    private BookInfoRepository bookInfoRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void clear() {
        bookRepository.deleteAll();
        bookshelfRepository.deleteAll();
        userRepository.deleteAll();
        bookInfoRepository.deleteAll();
    }

    @Test
    @DisplayName("getAll ???????????? ????????? ????????? ?????? ????????? ????????????.")
    void get_all() {
        // given
        User user = userRepository.save(user());

        Bookshelf bookshelf1 = Bookshelf.builder()
                .isOpen(true)
                .user(user)
                .name("????????? ??????")
                .build();

        Bookshelf bookshelf2 = Bookshelf.builder()
                .isOpen(true)
                .user(user)
                .name("?????? ??????")
                .build();

        bookshelfRepository.saveAll(List.of(bookshelf1, bookshelf2));

        // expected
        List<Bookshelf> bookShelves = bookShelfService.getAll(user.getId());
        assertThat(bookShelves).hasSize(2);
    }

    @Test
    @DisplayName("create ???????????? ????????? ????????? ????????????.")
    void create() {
        // given
        User user = user();
        userRepository.save(user);

        // when
        BookshelfCreate bookShelfCreate = new BookshelfCreate("?????? ?????? ??????", true);
        bookShelfService.create(user.getId(), bookShelfCreate);

        // then
        List<Bookshelf> bookShelves = bookShelfService.getAll(user.getId());
        assertThat(bookShelves).hasSize(1);
    }

    @Test
    @DisplayName("create ???????????? ?????? ?????? ?????? ????????? ???????????? ????????? ?????????.")
    void invalid_creation_policy() {
        // given
        User user = user();
        userRepository.save(user);

        IntStream.range(0, BookshelfCreationPolicy.MAXIMUM_SIZE_LIMIT)
                .mapToObj(i -> new BookshelfCreate("?????? " + i, true))
                .forEach(bookshelfCreate -> bookShelfService.create(user.getId(), bookshelfCreate));

        // expected
        assertThatThrownBy(() -> bookShelfService.create(user.getId(), new BookshelfCreate("??????", true)))
                .isInstanceOf(InvalidBookshelfCreationPolicy.class);
    }

    @Test
    @DisplayName("update ???????????? ?????? ????????? ?????? ????????? ????????????.")
    void update() {
        // given
        User user = userRepository.save(user());
        Bookshelf bookshelf = bookshelfRepository.save(bookshelf(user));

        // when
        BookshelfUpdate bookshelfUpdate = new BookshelfUpdate("????????? ??????", false);
        bookShelfService.update(bookshelf.getId(), user.getId(), bookshelfUpdate);

        // then
        Bookshelf found = bookshelfRepository.getByIdAndUserId(bookshelf.getId(), user.getId());
        assertThat(found.getName()).isEqualTo("????????? ??????");
        assertThat(found.isOpen()).isFalse();
    }

    @Test
    @DisplayName("delete ???????????? ????????? ????????????.")
    void delete() {
        // given
        User user = userRepository.save(user());
        Bookshelf bookshelf = bookshelfRepository.save(bookshelf(user));

        // expected
        List<Bookshelf> beforeDeleted = bookShelfService.getAll(user.getId());
        bookShelfService.delete(bookshelf.getId(), user.getId());
        List<Bookshelf> afterDeleted = bookShelfService.getAll(user.getId());

        assertThat(beforeDeleted.size() - 1).isEqualTo(afterDeleted.size());
    }

    @Test
    @DisplayName("delete ???????????? ????????? ??????????????? ????????? ???????????? ?????? ?????? ?????? ?????? ????????????.")
    @Transactional
    void delete_cascade() {
        // given
        User user = userRepository.save(user());
        Bookshelf bookshelf = bookshelfRepository.save(bookshelf(user));
        List<BookInfo> bookInfoList = bookInfoRepository.saveAll(bookInfo(5, Genre.BUSINESS, BookType.NON_FICTION));
        List<Book> bookList = bookRepository.saveAll(book(bookshelf, user, bookInfoList));

        // when
        bookShelfService.delete(bookshelf.getId(), user.getId());

        // then
        bookList.forEach(book ->
                assertThatThrownBy(() -> bookRepository.getByIdAndUserId(book.getId(), user.getId()))
                        .isInstanceOf(BookNotFoundException.class)
        );
    }
}
