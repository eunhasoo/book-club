package com.eunhasoo.bookclub.review.application;

import com.eunhasoo.bookclub.book.domain.BookInfo;
import com.eunhasoo.bookclub.book.domain.BookInfoRepository;
import com.eunhasoo.bookclub.exception.review.CommentNotFoundException;
import com.eunhasoo.bookclub.helper.FixtureList;
import com.eunhasoo.bookclub.review.domain.Comment;
import com.eunhasoo.bookclub.review.domain.CommentRepository;
import com.eunhasoo.bookclub.review.domain.Review;
import com.eunhasoo.bookclub.review.domain.ReviewRepository;
import com.eunhasoo.bookclub.review.ui.request.CommentCreate;
import com.eunhasoo.bookclub.review.ui.request.CommentEdit;
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
        CommentSearch commentSearch = new CommentSearch(1);
        List<Comment> comments = commentService.getComments(review.getId(), commentSearch);

        comments.stream().map(CommentListResponse::new).collect(Collectors.toUnmodifiableList());
        // then
        assertThat(comments.size()).isEqualTo(commentSearch.getSize());
        assertThat(comments.get(0).getId()).isLessThan(comments.get(comments.size() - 1).getId());
    }

    @Test
    @DisplayName("edit 메소드는 작성한 댓글의 내용을 수정한다.")
    void edit_comment() {
        // given
        BookInfo bookinfo = bookInfoRepository.save(bookInfo());
        User user = userRepository.save(userWithEncodedPassword());
        Review review = reviewRepository.save(review(bookinfo, user));
        Comment comment = commentRepository.save(comment(review, user));

        // when
        CommentEdit commentEdit = new CommentEdit("수정된 댓글");
        commentService.edit(comment.getId(), user.getId(), commentEdit);

        // then
        Comment afterEdit = commentRepository.getByIdAndUserId(comment.getId(), user.getId());
        assertThat(afterEdit.getContent()).isEqualTo("수정된 댓글");
        assertThat(comment.getUpdatedDate()).isNotEqualTo(afterEdit.getUpdatedDate());
    }

    @Test
    @DisplayName("edit 메소드는 다른 사용자의 댓글을 수정하려고 하면 에러를 던진다.")
    void edit_comment_fail() {
        // given
        BookInfo bookinfo = bookInfoRepository.save(bookInfo());
        User user = userRepository.save(userWithEncodedPassword());
        Review review = reviewRepository.save(review(bookinfo, user));
        Comment comment = commentRepository.save(comment(review, user));

        User anotherUser = userRepository.save(User.builder()
                .email("hello@gmail.com")
                .password("hello")
                .username("hello123")
                .build());

        // expected
        CommentEdit commentEdit = new CommentEdit("수정된 댓글");
        assertThatThrownBy(() -> commentService.edit(comment.getId(), anotherUser.getId(), commentEdit))
                .isInstanceOf(CommentNotFoundException.class);
    }

    @Test
    @DisplayName("delete 메소드는 작성한 댓글을 삭제한다.")
    void delete_comment() {
        // given
        BookInfo bookinfo = bookInfoRepository.save(bookInfo());
        User user = userRepository.save(userWithEncodedPassword());
        Review review = reviewRepository.save(review(bookinfo, user));
        Comment comment = commentRepository.save(comment(review, user));

        // when
        commentService.delete(comment.getId(), user.getId());

        // then
        assertThatThrownBy(() -> commentRepository.getByIdAndUserId(comment.getId(), user.getId()))
                .isInstanceOf(CommentNotFoundException.class);
    }

    @Test
    @DisplayName("delete 메소드는 다른 사용자의 댓글을 삭제하려고 하면 에러를 던진다.")
    void delete_comment_with_wrong_writer() {
        // given
        BookInfo bookinfo = bookInfoRepository.save(bookInfo());
        User user = userRepository.save(userWithEncodedPassword());
        Review review = reviewRepository.save(review(bookinfo, user));
        Comment comment = commentRepository.save(comment(review, user));

        User anotherUser = userRepository.save(User.builder()
                .email("hello@gmail.com")
                .password("hello")
                .username("hello123")
                .build());

        // expected
        assertThatThrownBy(() -> commentService.delete(comment.getId(), anotherUser.getId()))
                .isInstanceOf(CommentNotFoundException.class);
    }
}
