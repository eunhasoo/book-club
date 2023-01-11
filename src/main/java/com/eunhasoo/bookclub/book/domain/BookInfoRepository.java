package com.eunhasoo.bookclub.book.domain;

import com.eunhasoo.bookclub.exception.book.BookInfoNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookInfoRepository extends JpaRepository<BookInfo, Long> {
    Optional<BookInfo> findByIsbn(String isbn);

    default BookInfo getById(Long id) {
        return findById(id)
                .orElseThrow(() -> new BookInfoNotFoundException(id));
    }
}
