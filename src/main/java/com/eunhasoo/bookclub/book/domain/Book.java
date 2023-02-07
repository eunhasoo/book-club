package com.eunhasoo.bookclub.book.domain;

import com.eunhasoo.bookclub.common.BaseTime;
import com.eunhasoo.bookclub.user.domain.User;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Getter
@Entity
public class Book extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReadProcess readProcess;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_info_id", nullable = false)
    private BookInfo bookInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bookshelf_id", nullable = false)
    private Bookshelf bookshelf;

    protected Book() {
    }

    @Builder
    public Book(BookInfo bookInfo,
                User user,
                Bookshelf bookshelf,
                ReadProcess readProcess) {
        this.bookInfo = bookInfo;
        this.user = user;
        this.bookshelf = bookshelf;
        this.readProcess = readProcess != null ? readProcess : ReadProcess.BEFORE_READING;
    }

    public void updateReadProcess(ReadProcess readProcess) {
        this.readProcess = readProcess;
    }
}
