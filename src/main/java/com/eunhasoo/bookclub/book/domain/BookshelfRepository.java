package com.eunhasoo.bookclub.book.domain;

import com.eunhasoo.bookclub.exception.book.BookshelfNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BookshelfRepository extends JpaRepository<Bookshelf, Long> {

    int countAllByUserId(Long userId);

    @Query("select b, u.id from Bookshelf b left join b.user u where b.id = :id")
    Optional<Bookshelf> findById(Long id);

    Optional<Bookshelf> findByIdAndUserId(Long id, Long userId);

    List<Bookshelf> findAllByUserId(Long userId);

    default Bookshelf getById(Long id) {
        return findById(id)
                .orElseThrow(() -> new BookshelfNotFoundException(id));
    }

    default Bookshelf getByIdAndUserId(Long id, Long userId) {
        return findByIdAndUserId(id, userId)
                .orElseThrow(() -> new BookshelfNotFoundException(id, userId));
    }
}
