package com.eunhasoo.bookclub.book.ui.request;

import com.eunhasoo.bookclub.book.domain.BookInfo;
import com.eunhasoo.bookclub.book.domain.BookType;
import com.eunhasoo.bookclub.book.domain.Genre;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Getter
public class BookInfoCreate {

    @NotBlank(message = "ISBN을 입력해주세요.")
    @Size(min = 13, max = 13, message = "ISBN을 13자로 정확하게 입력해주세요.")
    private String isbn;

    @NotBlank(message = "책 이름을 입력해주세요.")
    @Size(max = 70, message = "책 이름은 70자를 넘을 수 없습니다.")
    private String name;

    @NotBlank(message = "작가명을 입력해주세요.")
    @Size(max = 30, message = "작가명은 30자를 넘을 수 없습니다.")
    private String author;

    @NotNull(message = "유형을 선택해주세요.")
    private BookType bookType;

    @NotNull(message = "장르를 선택해주세요.")
    private Genre genre;

    @Size(max = 20, message = "출판사명은 20자를 넘을 수 없습니다")
    private String publisher;

    private LocalDate publishedDate;

    @Size(max = 1000, message = "이미지의 길이는 1000자를 넘을 수 없습니다")
    private String imageUrl;

    private BookInfoCreate() {
    }

    @Builder
    public BookInfoCreate(String isbn,
                          String name,
                          String author,
                          BookType bookType,
                          Genre genre,
                          String publisher,
                          LocalDate publishedDate,
                          String imageUrl) {
        this.isbn = isbn;
        this.name = name;
        this.author = author;
        this.bookType = bookType;
        this.genre = genre;
        this.publisher = publisher;
        this.publishedDate = publishedDate;
        this.imageUrl = imageUrl;
    }

    public BookInfo toEntity() {
        return BookInfo.builder()
                .isbn(isbn)
                .name(name)
                .author(author)
                .bookType(bookType)
                .genre(genre)
                .publisher(publisher)
                .publishedDate(publishedDate)
                .imageUrl(imageUrl)
                .build();
    }
}
