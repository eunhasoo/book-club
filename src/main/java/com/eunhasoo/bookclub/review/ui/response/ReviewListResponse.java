package com.eunhasoo.bookclub.review.ui.response;

import com.eunhasoo.bookclub.book.domain.BookInfo;
import com.eunhasoo.bookclub.review.domain.Review;
import com.eunhasoo.bookclub.user.domain.User;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReviewListResponse {
    private WriterResource reviewer;
    private ReviewResource review;
    private BookResource bookInfo;

    public ReviewListResponse(Review review) {
        this.review = new ReviewResource(review);
        this.reviewer = new WriterResource(review.getReviewer());
        this.bookInfo = new BookResource(review.getBookInfo());
    }

    @Getter
    private static class WriterResource {
        private Long userId;
        private String nickname;

        public WriterResource(User user) {
            this.userId = user.getId();
            this.nickname = user.getNickname();
        }
    }

    @Getter
    private static class BookResource {
        private Long bookInfoId;
        private String name;
        private String author;
        private String imageUrl;

        public BookResource(BookInfo bookInfo) {
            this.bookInfoId = bookInfo.getId();
            this.name = bookInfo.getName();
            this.author = bookInfo.getAuthor();
            this.imageUrl = bookInfo.getImageUrl();
        }
    }

    @Getter
    private static class ReviewResource {
        private Long reviewId;
        private int score;
        private String title;
        private String content;
        private LocalDateTime createdDate;

        public ReviewResource(Review review) {
            this.reviewId = review.getId();
            this.score = review.getScore();
            this.createdDate = review.getCreatedDate();
            this.title = review.getTitle();
            this.content = review.getContent();
        }
    }
}
