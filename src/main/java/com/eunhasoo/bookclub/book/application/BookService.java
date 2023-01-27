package com.eunhasoo.bookclub.book.application;

import com.eunhasoo.bookclub.book.domain.Book;
import com.eunhasoo.bookclub.book.domain.BookInfo;
import com.eunhasoo.bookclub.book.domain.BookInfoRepository;
import com.eunhasoo.bookclub.book.domain.BookRepository;
import com.eunhasoo.bookclub.book.domain.Bookshelf;
import com.eunhasoo.bookclub.book.domain.BookshelfRepository;
import com.eunhasoo.bookclub.book.ui.request.BookCreate;
import com.eunhasoo.bookclub.book.ui.request.BookSearch;
import com.eunhasoo.bookclub.book.ui.request.BookUpdate;
import com.eunhasoo.bookclub.exception.book.BookAlreadyExistException;
import com.eunhasoo.bookclub.user.domain.User;
import com.eunhasoo.bookclub.user.domain.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@Service
public class BookService {

    private final BookInfoRepository bookInfoRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final BookshelfRepository bookShelfRepository;

    public BookService(BookInfoRepository bookInfoRepository,
                       UserRepository userRepository,
                       BookRepository bookRepository,
                       BookshelfRepository bookShelfRepository) {
        this.bookInfoRepository = bookInfoRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.bookShelfRepository = bookShelfRepository;
    }

    public Book get(Long userId, Long bookId) {
        return bookRepository.getByIdAndUserId(bookId, userId);
    }

    @Transactional
    public void create(Long userId, BookCreate bookCreate) {
        Long bookInfoId = bookCreate.getBookInfoId();
        Long bookshelfId = bookCreate.getBookShelfId();

        checkIfBookIsAlreadyInBookshelf(bookInfoId, bookshelfId);

        BookInfo bookInfo = bookInfoRepository.getById(bookInfoId);
        Bookshelf bookshelf = bookShelfRepository.getByIdAndUserId(bookshelfId, userId);
        User user = userRepository.getById(userId);

        Book book = Book.builder()
                .bookInfo(bookInfo)
                .user(user)
                .bookshelf(bookshelf)
                .readProcess(bookCreate.getReadProcess())
                .build();

        bookRepository.save(book);
    }

    private void checkIfBookIsAlreadyInBookshelf(Long bookInfoId, Long bookshelfId) {
        if (bookRepository.existsByBookInfoIdAndBookshelfId(bookInfoId, bookshelfId)) {
            throw new BookAlreadyExistException();
        }
    }

    @Transactional
    public void delete(Long userId, Long bookId) {
        Book book = bookRepository.getByIdAndUserId(bookId, userId);

        bookRepository.delete(book);
    }

    @Transactional
    public void update(Long userId, Long bookId, BookUpdate bookUpdate) {
        Book book = bookRepository.getByIdAndUserId(bookId, userId);

        book.updateReadProcess(bookUpdate.getReadProcess());
    }

    public List<Book> getAll(Long userId, BookSearch bookSearch) {
        Bookshelf bookshelf = bookShelfRepository.getById(bookSearch.getBookshelfId());

        bookshelf.checkAccessibility(userId);

        return bookRepository.getList(bookSearch);
    }
}
