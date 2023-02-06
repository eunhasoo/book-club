package com.eunhasoo.bookclub.review.ui;

import com.eunhasoo.bookclub.auth.CustomUserDetails;
import com.eunhasoo.bookclub.auth.CustomUserDetailsService;
import com.eunhasoo.bookclub.auth.jwt.TokenProvider;
import com.eunhasoo.bookclub.book.domain.BookInfo;
import com.eunhasoo.bookclub.book.domain.BookInfoRepository;
import com.eunhasoo.bookclub.book.domain.BookType;
import com.eunhasoo.bookclub.book.domain.Genre;
import com.eunhasoo.bookclub.helper.FixtureList;
import com.eunhasoo.bookclub.review.domain.Comment;
import com.eunhasoo.bookclub.review.domain.CommentRepository;
import com.eunhasoo.bookclub.review.domain.Review;
import com.eunhasoo.bookclub.review.domain.ReviewRepository;
import com.eunhasoo.bookclub.review.ui.request.ReviewCreate;
import com.eunhasoo.bookclub.review.ui.request.ReviewUpdate;
import com.eunhasoo.bookclub.user.domain.User;
import com.eunhasoo.bookclub.user.domain.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.eunhasoo.bookclub.helper.Fixture.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ReviewControllerTest {

    private static final String REVIEW_API = "/api/reviews";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private BookInfoRepository bookInfoRepository;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private TokenProvider tokenProvider;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    @AfterEach
    void clear() {
        commentRepository.deleteAll();
        reviewRepository.deleteAll();
        bookInfoRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName(REVIEW_API + " GET 요청시 페이징 처리된 최신 리뷰 데이터와 함께 200 응답 코드를 생성한다.")
    void get_reviews() throws Exception {
        // given
        User user = userRepository.save(userWithEncodedPassword());

        int size = 10;
        List<BookInfo> fantasyFic = FixtureList.bookInfo(size, Genre.FANTASY, BookType.FICTION);
        bookInfoRepository.saveAll(fantasyFic);

        List<Review> reviews = FixtureList.review(fantasyFic, user);
        reviewRepository.saveAll(reviews);

        Review lastReview = reviews.get(reviews.size() - 1);

        // expect
        mockMvc.perform(get(REVIEW_API))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(5))
                .andExpect(jsonPath("$.data[0].review.reviewId").value(lastReview.getId()))
                .andExpect(jsonPath("$.data[4].review.reviewId").value(lastReview.getId() - 4))
                .andDo(print());
    }

    @Test
    @DisplayName(REVIEW_API + " POST 요청시 리뷰를 저장하고 201 응답 코드를 생성한다.")
    void write_review() throws Exception {
        // given
        User user = userRepository.save(userWithEncodedPassword());
        BookInfo bookInfo = bookInfoRepository.save(bookInfo());

        when(tokenProvider.validateToken(anyString())).thenReturn(true);
        when(tokenProvider.createToken(anyLong())).thenReturn("token");
        when(userDetailsService.loadUserByUserId(anyLong())).thenReturn(CustomUserDetails.create(user));
        when(tokenProvider.getUserIdFromToken(anyString())).thenReturn(user.getId());

        ReviewCreate reviewCreate = new ReviewCreate(bookInfo.getId(), "title", "content", 5);

        // expect
        mockMvc.perform(post(REVIEW_API)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                        .content(om.writeValueAsString(reviewCreate))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @Test
    @DisplayName(REVIEW_API + "/{reviewId} GET 요청시 리뷰 데이터와 200 응답 코드를 생성한다.")
    @Transactional
    void get_review() throws Exception {
        // given
        User user = userRepository.save(userWithEncodedPassword());
        BookInfo bookInfo = bookInfoRepository.save(bookInfo());
        Review review = reviewRepository.save(review(bookInfo, user));

        when(tokenProvider.validateToken(anyString())).thenReturn(true);
        when(tokenProvider.createToken(anyLong())).thenReturn("token");
        when(userDetailsService.loadUserByUserId(anyLong())).thenReturn(CustomUserDetails.create(user));
        when(tokenProvider.getUserIdFromToken(anyString())).thenReturn(user.getId());

        // expect
        mockMvc.perform(get(REVIEW_API + "/{reviewId}", review.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.reviewId").value(review.getId()),
                        jsonPath("$.reviewer").value(review.getReviewer().getNickname()),
                        jsonPath("$.score").value(review.getScore()),
                        jsonPath("$.title").value(review.getTitle()),
                        jsonPath("$.content").value(review.getContent())
                )
                .andDo(print());
    }

    @Test
    @DisplayName(REVIEW_API + "/{reviewId} GET 요청시 리뷰를 찾을 수 없으면 404 응답 코드를 생성한다.")
    @Transactional
    void get_not_exist_review() throws Exception {
        // given
        User user = userRepository.save(userWithEncodedPassword());

        when(tokenProvider.validateToken(anyString())).thenReturn(true);
        when(tokenProvider.createToken(anyLong())).thenReturn("token");
        when(userDetailsService.loadUserByUserId(anyLong())).thenReturn(CustomUserDetails.create(user));
        when(tokenProvider.getUserIdFromToken(anyString())).thenReturn(user.getId());

        // expect
        mockMvc.perform(get(REVIEW_API + "/{reviewId}", 100L)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName(REVIEW_API + "/{reviewId} POST 요청시 리뷰를 수정하고 200 응답 코드를 생성한다.")
    @Transactional
    void update_review() throws Exception {
        // given
        User user = userRepository.save(userWithEncodedPassword());
        BookInfo bookInfo = bookInfoRepository.save(bookInfo());
        Review review = reviewRepository.save(review(bookInfo, user));

        when(tokenProvider.validateToken(anyString())).thenReturn(true);
        when(tokenProvider.createToken(anyLong())).thenReturn("token");
        when(userDetailsService.loadUserByUserId(anyLong())).thenReturn(CustomUserDetails.create(user));
        when(tokenProvider.getUserIdFromToken(anyString())).thenReturn(user.getId());

        // expect
        ReviewUpdate reviewUpdate = new ReviewUpdate("수정한 제목", "수정한 내용");

        mockMvc.perform(post(REVIEW_API + "/{reviewId}", review.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(reviewUpdate))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName(REVIEW_API + "/{reviewId} POST 요청시 다른 사람의 리뷰를 수정하려고 하면 403 응답 코드를 생성한다.")
    @Transactional
    void update_review_fail() throws Exception {
        // given
        User user = userRepository.save(userWithEncodedPassword());
        BookInfo bookInfo = bookInfoRepository.save(bookInfo());
        Review review = reviewRepository.save(review(bookInfo, user));

        User anotherUser = userRepository.save(User.builder()
                .username("another_user")
                .email("another_user@gmail.com")
                .password("another-user-300")
                .build());

        when(tokenProvider.validateToken(anyString())).thenReturn(true);
        when(tokenProvider.createToken(anyLong())).thenReturn("token");
        when(userDetailsService.loadUserByUserId(anyLong())).thenReturn(CustomUserDetails.create(anotherUser));
        when(tokenProvider.getUserIdFromToken(anyString())).thenReturn(anotherUser.getId());

        // expect
        ReviewUpdate reviewUpdate = new ReviewUpdate("수정한 제목", "수정한 내용");

        mockMvc.perform(post(REVIEW_API + "/{reviewId}", review.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(reviewUpdate))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @DisplayName(REVIEW_API + "/{reviewId} DELETE 요청시 리뷰를 삭제하고 204 응답 코드를 생성한다.")
    @Transactional
    void delete_review() throws Exception {
        // given
        User user = userRepository.save(userWithEncodedPassword());
        BookInfo bookInfo = bookInfoRepository.save(bookInfo());
        Review review = reviewRepository.save(review(bookInfo, user));

        when(tokenProvider.validateToken(anyString())).thenReturn(true);
        when(tokenProvider.createToken(anyLong())).thenReturn("token");
        when(userDetailsService.loadUserByUserId(anyLong())).thenReturn(CustomUserDetails.create(user));
        when(tokenProvider.getUserIdFromToken(anyString())).thenReturn(user.getId());

        // expect
        mockMvc.perform(delete(REVIEW_API + "/{reviewId}", review.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    @DisplayName(REVIEW_API + "/{reviewId} DELETE 요청시 리뷰를 찾을 수 없으면 403 응답 코드를 생성한다.")
    @Transactional
    void delete_review_fail_() throws Exception {
        // given
        User user = userRepository.save(userWithEncodedPassword());

        when(tokenProvider.validateToken(anyString())).thenReturn(true);
        when(tokenProvider.createToken(anyLong())).thenReturn("token");
        when(userDetailsService.loadUserByUserId(anyLong())).thenReturn(CustomUserDetails.create(user));
        when(tokenProvider.getUserIdFromToken(anyString())).thenReturn(user.getId());

        // expect
        mockMvc.perform(delete(REVIEW_API + "/{reviewId}", 100L)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @DisplayName(REVIEW_API + "/{reviewId} DELETE 요청시 다른 사람의 리뷰를 삭제하려고 하면 403 응답 코드를 생성한다.")
    @Transactional
    void delete_review_fail() throws Exception {
        // given
        User user = userRepository.save(userWithEncodedPassword());
        BookInfo bookInfo = bookInfoRepository.save(bookInfo());
        Review review = reviewRepository.save(review(bookInfo, user));

        User anotherUser = userRepository.save(User.builder()
                .username("another_user")
                .email("another_user@gmail.com")
                .password("another-user-300")
                .build());

        when(tokenProvider.validateToken(anyString())).thenReturn(true);
        when(tokenProvider.createToken(anyLong())).thenReturn("token");
        when(userDetailsService.loadUserByUserId(anyLong())).thenReturn(CustomUserDetails.create(anotherUser));
        when(tokenProvider.getUserIdFromToken(anyString())).thenReturn(anotherUser.getId());

        // expect
        mockMvc.perform(delete(REVIEW_API + "/{reviewId}", review.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @DisplayName(REVIEW_API + "/{reviewId}/comments GET 요청시 페이징 처리된 리뷰의 댓글 목록과 함께 200 응답 코드를 생성한다.")
    void get_comments() throws Exception {
        // given
        BookInfo bookinfo = bookInfoRepository.save(bookInfo());
        User user = userRepository.save(userWithEncodedPassword());
        Review review = reviewRepository.save(review(bookinfo, user));
        List<Comment> comments = commentRepository.saveAll(FixtureList.comment(30, review, user));

        when(tokenProvider.validateToken(anyString())).thenReturn(true);
        when(tokenProvider.createToken(anyLong())).thenReturn("token");
        when(userDetailsService.loadUserByUserId(anyLong())).thenReturn(CustomUserDetails.create(user));
        when(tokenProvider.getUserIdFromToken(anyString())).thenReturn(user.getId());

        mockMvc.perform(get(REVIEW_API + "/{reviewId}/comments", review.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                        .param("page", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(20))
                .andExpect(jsonPath("$.data[0].commentId").value(comments.get(0).getId()))
                .andExpect(jsonPath("$.data[19].commentId").value(comments.get(19).getId()))
                .andDo(print());
    }
}
