package com.eunhasoo.bookclub.helper;

import com.eunhasoo.bookclub.book.domain.Book;
import com.eunhasoo.bookclub.book.domain.BookInfo;
import com.eunhasoo.bookclub.book.domain.BookType;
import com.eunhasoo.bookclub.book.domain.Bookshelf;
import com.eunhasoo.bookclub.book.domain.Genre;
import com.eunhasoo.bookclub.book.domain.ReadProcess;
import com.eunhasoo.bookclub.review.domain.Comment;
import com.eunhasoo.bookclub.review.domain.Review;
import com.eunhasoo.bookclub.user.domain.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FixtureList {

    private static String getRandomIsbn(int seed){
        Random random = new Random(seed);
        final char[] ch = new char[13];
        for(int i = 0; i < 13; i++){
            ch[i] = (char) ('0' + (i == 0 ? random.nextInt(9) + 1 : random.nextInt(10)));
        }
        return new String(ch);
    }

    public static List<BookInfo> bookInfo(int size, Genre genre, BookType bookType) {
        return IntStream.range(0, size)
                .mapToObj(i -> BookInfo.builder()
                                .name("Book Title " + i)
                                .bookType(bookType)
                                .genre(genre)
                                .author("Adams")
                                .imageUrl("/img/book_cover_" + i)
                                .isbn(getRandomIsbn(i))
                                .publisher("ABC Books")
                                .publishedDate(LocalDate.now())
                                .build())
                .collect(Collectors.toUnmodifiableList());
    }

    public static List<Book> book(Bookshelf bookShelf, User user, List<BookInfo> bookInfoList) {
        return bookInfoList.stream()
                .map(bookInfo -> Book.builder()
                        .bookshelf(bookShelf)
                        .user(user)
                        .bookInfo(bookInfo)
                        .readProcess(ReadProcess.BEFORE_READING)
                        .build())
                .collect(Collectors.toUnmodifiableList());
    }

    public static List<Review> review(List<BookInfo> bookInfoList, User user) {
        return IntStream.range(0, bookInfoList.size())
                .mapToObj(i -> Review.builder()
                        .title("title " + i)
                        .content("content " + i)
                        .reviewer(user)
                        .bookInfo(bookInfoList.get(i))
                        .score((int) Math.random() * 5 + 1)
                        .build())
                .collect(Collectors.toUnmodifiableList());
    }

    public static List<Comment> comment(int size, Review review, User user) {
        return IntStream.range(0, size)
                .mapToObj(i -> Comment.builder()
                        .content("comment " + i)
                        .user(user)
                        .review(review)
                        .build())
                .collect(Collectors.toUnmodifiableList());
    }
}
