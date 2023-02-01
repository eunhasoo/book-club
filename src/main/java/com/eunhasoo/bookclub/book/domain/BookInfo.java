package com.eunhasoo.bookclub.book.domain;

import lombok.Builder;
import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Table(name = "book_info", indexes = @Index(columnList = "isbn"))
@Entity
public class BookInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 13, nullable = false, unique = true, updatable = false)
    private String isbn;

    @Column(length = 70, nullable = false)
    private String name;

    @Column(length = 30, nullable = false)
    private String author;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookType bookType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Genre genre;

    @Column(nullable = false)
    private LocalDateTime createdDate;

    private String publisher;

    private LocalDate publishedDate;

    private String imageUrl;

    protected BookInfo() {
    }

    @Builder
    public BookInfo(String isbn,
                    String name,
                    String author,
                    String publisher,
                    LocalDate publishedDate,
                    String imageUrl,
                    BookType bookType,
                    Genre genre) {
        this.isbn = isbn;
        this.name = name;
        this.author = author;
        this.publisher = publisher;
        this.publishedDate = publishedDate;
        this.imageUrl = imageUrl;
        this.bookType = bookType;
        this.genre = genre;
        this.createdDate = LocalDateTime.now();
    }
}
