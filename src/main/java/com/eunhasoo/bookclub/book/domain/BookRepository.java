package com.eunhasoo.bookclub.book.domain;

import com.eunhasoo.bookclub.exception.book.BookNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long>, BookRepositoryCustom {

    @Query("select b from Book b where b.id = :bookId and b.user.id = :userId")
    Optional<Book> findByIdAndUserId(Long bookId, Long userId);

    boolean existsByBookInfoIdAndBookshelfId(Long bookInfoId, Long bookshelfId);

    @Query("select b from Book b where b.bookshelf.id = :bookshelfId")
    List<Book> findAllByBookshelfId(Long bookshelfId);

    default Book getByIdAndUserId(Long bookId, Long userId) {
        return findByIdAndUserId(bookId, userId)
                .orElseThrow(() -> new BookNotFoundException(bookId, userId));
    }
}
