package com.eunhasoo.bookclub.helper;

import com.eunhasoo.bookclub.book.domain.Book;
import com.eunhasoo.bookclub.book.domain.BookInfo;
import com.eunhasoo.bookclub.book.domain.Bookshelf;
import com.eunhasoo.bookclub.book.domain.BookType;
import com.eunhasoo.bookclub.book.domain.Genre;
import com.eunhasoo.bookclub.book.domain.ReadProcess;
import com.eunhasoo.bookclub.user.domain.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

public class Fixture {

    private static final PasswordEncoder encoder = new BCryptPasswordEncoder();

    public static BookInfo bookInfo() {
        return BookInfo.builder()
                .name("Harry Potter")
                .bookType(BookType.FICTION)
                .genre(Genre.FANTASY)
                .author("J.K. Rowling")
                .imageUrl("/img/harry_potter")
                .isbn("1234567891234")
                .publisher("Scholastic")
                .publishedDate(LocalDate.now())
                .build();
    }

    public static User user() {
        return User.builder()
                .username("tester")
                .email("test@email.com")
                .password("test12345")
                .build();
    }

    public static User userWithEncodedPassword() {
        return User.builder()
                .username("tester")
                .email("test@email.com")
                .password(encoder.encode("test12345"))
                .build();
    }

    public static Bookshelf bookshelf(User user) {
        return Bookshelf.builder()
                .isOpen(true)
                .user(user)
                .name("must read in 20s")
                .build();
    }

    public static Book book(Bookshelf bookShelf, User user, BookInfo bookInfo) {
        return Book.builder()
                .bookshelf(bookShelf)
                .user(user)
                .bookInfo(bookInfo)
                .readProcess(ReadProcess.BEFORE_READING)
                .build();
    }

}
