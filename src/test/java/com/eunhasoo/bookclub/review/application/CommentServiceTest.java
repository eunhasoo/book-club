package com.eunhasoo.bookclub.review.application;

import com.eunhasoo.bookclub.book.domain.BookInfo;
import com.eunhasoo.bookclub.book.domain.BookInfoRepository;
import com.eunhasoo.bookclub.helper.Fixture;
import com.eunhasoo.bookclub.helper.FixtureList;
import com.eunhasoo.bookclub.review.domain.Comment;
import com.eunhasoo.bookclub.review.domain.CommentRepository;
import com.eunhasoo.bookclub.review.domain.Review;
import com.eunhasoo.bookclub.review.domain.ReviewRepository;
import com.eunhasoo.bookclub.review.ui.request.CommentCreate;
import com.eunhasoo.bookclub.review.ui.request.CommentSearch;
import com.eunhasoo.bookclub.review.ui.response.CommentListResponse;
import com.eunhasoo.bookclub.user.domain.User;
import com.eunhasoo.bookclub.user.domain.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.stream.Collectors;

import static com.eunhasoo.bookclub.helper.Fixture.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class CommentServiceTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private BookInfoRepository bookInfoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentService commentService;

    @AfterEach
    void clear() {
        commentRepository.deleteAll();
        reviewRepository.deleteAll();
        bookInfoRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("create 메소드는 리뷰에 대한 댓글을 생성한다.")
    void create_comment() {
        // given
        BookInfo bookinfo = bookInfoRepository.save(bookInfo());
        User user = userRepository.save(userWithEncodedPassword());
        Review review = reviewRepository.save(review(bookinfo, user));
        long beforeCreateCommentSize = commentRepository.count();

        // when
        commentService.create(user.getId(), new CommentCreate(review.getId(), "리뷰 댓글"));

        // then
        long afterCreateCommentSize = commentRepository.count();
        assertThat(beforeCreateCommentSize).isZero();
        assertThat(afterCreateCommentSize).isEqualTo(1);
    }

    @Test
    @DisplayName("getComments 메소드는 리뷰에 대한 댓글을 페이징 처리하여 작성일 오름차순으로 반환한다.")
    void get_comments() {
        // given
        BookInfo bookinfo = bookInfoRepository.save(bookInfo());
        User user = userRepository.save(userWithEncodedPassword());
        Review review = reviewRepository.save(review(bookinfo, user));
        commentRepository.saveAll(FixtureList.comment(30, review, user));

        // when
        CommentSearch commentSearch = new CommentSearch(review.getId(), 1);
        List<Comment> comments = commentService.getComments(commentSearch);

        comments.stream().map(CommentListResponse::new).collect(Collectors.toUnmodifiableList());
        // then
        assertThat(comments.size()).isEqualTo(commentSearch.getSize());
        assertThat(comments.get(0).getId()).isLessThan(comments.get(comments.size() - 1).getId());
    }
}
