package com.eunhasoo.bookclub.book.application;

import com.eunhasoo.bookclub.book.domain.Book;
import com.eunhasoo.bookclub.book.domain.BookRepository;
import com.eunhasoo.bookclub.book.domain.Bookshelf;
import com.eunhasoo.bookclub.book.domain.BookshelfCreationPolicy;
import com.eunhasoo.bookclub.book.domain.BookshelfRepository;
import com.eunhasoo.bookclub.book.ui.request.BookshelfCreate;
import com.eunhasoo.bookclub.book.ui.request.BookshelfUpdate;
import com.eunhasoo.bookclub.user.domain.User;
import com.eunhasoo.bookclub.user.domain.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@Service
public class BookshelfService {

    private final UserRepository userRepository;
    private final BookshelfRepository bookshelfRepository;
    private final BookshelfCreationPolicy creationPolicy;
    private final BookRepository bookRepository;

    public BookshelfService(UserRepository userRepository,
                            BookshelfRepository bookshelfRepository,
                            BookshelfCreationPolicy creationPolicy,
                            BookRepository bookRepository) {
        this.userRepository = userRepository;
        this.bookshelfRepository = bookshelfRepository;
        this.creationPolicy = creationPolicy;
        this.bookRepository = bookRepository;
    }

    public List<Bookshelf> getAll(Long userId) {
        return bookshelfRepository.findAllByUserId(userId);
    }

    @Transactional
    public void create(Long userId, BookshelfCreate bookshelfCreate) {
        checkCreationPolicy(userId);

        User user = userRepository.getById(userId);
        Bookshelf bookShelf = Bookshelf.builder()
                .user(user)
                .name(bookshelfCreate.getName())
                .isOpen(bookshelfCreate.isOpen())
                .build();

        bookshelfRepository.save(bookShelf);
    }

    private void checkCreationPolicy(Long userId) {
        int createdBookShelvesSize = bookshelfRepository.countAllByUserId(userId);
        creationPolicy.validate(createdBookShelvesSize);
    }

    @Transactional
    public void update(Long bookshelfId, Long userId, BookshelfUpdate bookshelfUpdate) {
        Bookshelf bookShelf = bookshelfRepository.getByIdAndUserId(bookshelfId, userId);
        bookShelf.update(bookshelfUpdate.getName(), bookshelfUpdate.getIsPublic());
    }

    @Transactional
    public void delete(Long bookshelfId, Long userId) {
        Bookshelf bookshelf = bookshelfRepository.getByIdAndUserId(bookshelfId, userId);

        List<Book> books = bookRepository.findAllByBookshelfId(bookshelfId);
        bookRepository.deleteAll(books);

        bookshelfRepository.delete(bookshelf);
    }
}
