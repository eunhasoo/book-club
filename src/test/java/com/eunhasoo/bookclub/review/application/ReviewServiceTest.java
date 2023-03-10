package com.eunhasoo.bookclub.review.application;

import com.eunhasoo.bookclub.book.domain.BookInfo;
import com.eunhasoo.bookclub.book.domain.BookInfoRepository;
import com.eunhasoo.bookclub.book.domain.BookRepository;
import com.eunhasoo.bookclub.book.domain.BookType;
import com.eunhasoo.bookclub.book.domain.Genre;
import com.eunhasoo.bookclub.exception.auth.DataAccessFailureException;
import com.eunhasoo.bookclub.exception.review.CommentNotFoundException;
import com.eunhasoo.bookclub.exception.review.ReviewAlreadyExistException;
import com.eunhasoo.bookclub.exception.review.ReviewNotFoundException;
import com.eunhasoo.bookclub.helper.FixtureList;
import com.eunhasoo.bookclub.review.domain.Comment;
import com.eunhasoo.bookclub.review.domain.CommentRepository;
import com.eunhasoo.bookclub.review.domain.Review;
import com.eunhasoo.bookclub.review.domain.ReviewRepository;
import com.eunhasoo.bookclub.review.ui.request.ReviewCreate;
import com.eunhasoo.bookclub.review.ui.request.ReviewSearch;
import com.eunhasoo.bookclub.review.ui.request.ReviewUpdate;
import com.eunhasoo.bookclub.user.domain.User;
import com.eunhasoo.bookclub.user.domain.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static com.eunhasoo.bookclub.helper.Fixture.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class ReviewServiceTest {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private BookInfoRepository bookInfoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CommentRepository commentRepository;

    @AfterEach
    void clear() {
        commentRepository.deleteAll();
        reviewRepository.deleteAll();
        bookRepository.deleteAll();
        bookInfoRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("write ???????????? ????????? ???????????? ???????????? ????????????.")
    void write_success() {
        // given
        User user = userRepository.save(userWithEncodedPassword());
        BookInfo bookInfo = bookInfoRepository.save(bookInfo());

        ReviewCreate reviewCreate = new ReviewCreate(bookInfo.getId(), "title", "content", 5);

        // when
        Long reviewId = reviewService.write(user.getId(), reviewCreate);

        // expected
        List<Review> reviews = reviewRepository.findAll();
        assertThat(reviews).hasSize(1);
        assertThat(reviews.get(0).getId()).isEqualTo(reviewId);
    }

    @Test
    @DisplayName("write ???????????? ????????? ????????? ????????? ????????? ?????? ?????? ????????? ???????????? ????????? ?????????.")
    void write_fail() {
        // given
        User user = userRepository.save(userWithEncodedPassword());
        BookInfo bookInfo = bookInfoRepository.save(bookInfo());
        reviewRepository.save(review(bookInfo, user));

        // expected
        ReviewCreate reviewCreate = new ReviewCreate(bookInfo.getId(), "title", "content", 5);

        assertThatThrownBy(() -> reviewService.write(user.getId(), reviewCreate))
                .isInstanceOf(ReviewAlreadyExistException.class);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1})
    @DisplayName("getReviews ???????????? ?????? 1???????????? ???????????? ????????????.")
    void get_all(int page) {
        // given
        User user = userRepository.save(userWithEncodedPassword());

        int size = 5;
        List<BookInfo> bookInfoList = FixtureList.bookInfo(size, Genre.FANTASY, BookType.FICTION);
        bookInfoRepository.saveAll(bookInfoList);

        reviewRepository.saveAll(FixtureList.review(bookInfoList, user));

        // when
        ReviewSearch reviewSearch = new ReviewSearch(page, null, null);
        List<Review> reviews = reviewService.getReviews(reviewSearch);

        // then
        assertThat(reviews).hasSize(size);
    }

    @Test
    @DisplayName("getReviews ???????????? ??????, ????????? ?????? ????????? ???????????? ????????????.")
    void get_by_filter() {
        // given
        User user = userRepository.save(userWithEncodedPassword());

        int size = 5;
        List<BookInfo> fantasyFic = FixtureList.bookInfo(size, Genre.FANTASY, BookType.FICTION);
        bookInfoRepository.saveAll(fantasyFic);

        reviewRepository.saveAll(FixtureList.review(fantasyFic, user));

        // when
        ReviewSearch medicalNonFicSearch = new ReviewSearch(1, Genre.MEDICAL, BookType.NON_FICTION);
        List<Review> medicalNonFicSearchResult = reviewService.getReviews(medicalNonFicSearch);

        ReviewSearch fantasyFicSearch = new ReviewSearch(1, Genre.FANTASY, BookType.FICTION);
        List<Review> fantasyFicSearchResult = reviewService.getReviews(fantasyFicSearch);

        // then
        assertThat(medicalNonFicSearchResult).hasSize(0);

        assertThat(fantasyFicSearchResult).hasSize(size);
    }

    @Test
    @DisplayName("getReview ???????????? ?????? ????????? ????????????.")
    void get_review() {
        // given
        User user = userRepository.save(userWithEncodedPassword());
        BookInfo bookInfo = bookInfoRepository.save(bookInfo());
        Review review = reviewRepository.save(review(bookInfo, user));

        // when
        Review found = reviewService.getReview(review.getId());

        // then
        Assertions.assertAll(
                () -> assertThat(found.getId()).isEqualTo(review.getId()),
                () -> assertThat(found.getTitle()).isEqualTo(review.getTitle()),
                () -> assertThat(found.getContent()).isEqualTo(review.getContent()),
                () -> assertThat(found.getReviewer().getId()).isEqualTo(review.getReviewer().getId()),
                () -> assertThat(found.getScore()).isEqualTo(review.getScore()),
                () -> assertThat(found.getBookInfo().getId()).isEqualTo(review.getBookInfo().getId())
        );
    }

    @Test
    @DisplayName("getReview ???????????? ????????? ?????? ????????? ????????? ?????????.")
    void get_review_fail() {
        assertThatThrownBy(() -> reviewService.getReview(1000L))
                .isInstanceOf(ReviewNotFoundException.class);
    }

    @Test
    @DisplayName("update ???????????? ????????? ????????? ????????? ????????????.")
    void update_review() {
        // given
        User user = userRepository.save(userWithEncodedPassword());
        BookInfo bookInfo = bookInfoRepository.save(bookInfo());
        Review review = reviewRepository.save(review(bookInfo, user));

        // when
        ReviewUpdate reviewUpdate = new ReviewUpdate("????????? ??????", "????????? ??????");
        reviewService.update(user.getId(), review.getId(), reviewUpdate);

        // then
        Review found = reviewRepository.getById(review.getId());
        assertThat(found.getTitle()).isEqualTo("????????? ??????");
        assertThat(found.getContent()).isEqualTo("????????? ??????");
    }

    @Test
    @DisplayName("update ???????????? ?????? ???????????? ?????? ???????????? ???????????? ????????? ????????? ?????????.")
    void update_review_access_fail() {
        // given
        User user = userRepository.save(userWithEncodedPassword());
        BookInfo bookInfo = bookInfoRepository.save(bookInfo());
        Review review = reviewRepository.save(review(bookInfo, user));

        User anotherUser = userRepository.save(User.builder()
                        .username("another_user")
                        .email("another_user@gmail.com")
                        .password("another!@#$")
                        .build());

        // expected
        ReviewUpdate reviewUpdate = new ReviewUpdate("????????? ??????", "????????? ??????");
        assertThatThrownBy(() -> reviewService.update(anotherUser.getId(), review.getId(), reviewUpdate))
                .isInstanceOf(DataAccessFailureException.class);
    }

    @Test
    @DisplayName("delete ???????????? ????????? ????????? ????????????.")
    void delete_review() {
        // given
        User user = userRepository.save(userWithEncodedPassword());
        BookInfo bookInfo = bookInfoRepository.save(bookInfo());
        Review review = reviewRepository.save(review(bookInfo, user));

        // when
        reviewService.delete(user.getId(), review.getId());

        // then
        assertThatThrownBy(() -> reviewRepository.getById(review.getId()))
                .isInstanceOf(ReviewNotFoundException.class);
    }

    @Test
    @DisplayName("delete ???????????? ????????? ??????????????? ????????? ????????? ????????? ?????? ?????????.")
    void delete_review_with_comments() {
        // given
        User user = userRepository.save(userWithEncodedPassword());
        BookInfo bookInfo = bookInfoRepository.save(bookInfo());
        Review review = reviewRepository.save(review(bookInfo, user));
        List<Comment> comments = commentRepository.saveAll(FixtureList.comment(30, review, user));

        // when
        reviewService.delete(user.getId(), review.getId());

        // then
        assertThatThrownBy(() ->
                comments.forEach(comment -> commentRepository.getByIdAndUserId(comment.getId(), user.getId()))
        ).isInstanceOf(CommentNotFoundException.class);
    }
}
