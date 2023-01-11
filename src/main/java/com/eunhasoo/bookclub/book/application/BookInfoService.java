package com.eunhasoo.bookclub.book.application;

import com.eunhasoo.bookclub.book.domain.BookInfo;
import com.eunhasoo.bookclub.book.domain.BookInfoRepository;
import com.eunhasoo.bookclub.book.ui.request.BookInfoCreate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
public class BookInfoService {

    private final BookInfoRepository bookInfoRepository;

    public BookInfoService(BookInfoRepository bookInfoRepository) {
        this.bookInfoRepository = bookInfoRepository;
    }

    public BookInfo get(Long id) {
        return bookInfoRepository.getById(id);
    }

    public Long getIdByIsbn(String isbn) {
        return bookInfoRepository.findByIsbn(isbn)
                .map(bookInfo -> bookInfo.getId())
                .orElse(null);
    }

    @Transactional
    public Long create(BookInfoCreate bookInfoCreate) {
        BookInfo bookInfo = bookInfoRepository.save(bookInfoCreate.toEntity());
        return bookInfo.getId();
    }
}
