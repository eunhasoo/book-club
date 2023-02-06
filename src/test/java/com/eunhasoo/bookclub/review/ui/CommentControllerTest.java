package com.eunhasoo.bookclub.review.ui;

import com.eunhasoo.bookclub.auth.CustomUserDetails;
import com.eunhasoo.bookclub.auth.CustomUserDetailsService;
import com.eunhasoo.bookclub.auth.jwt.TokenProvider;
import com.eunhasoo.bookclub.book.domain.BookInfo;
import com.eunhasoo.bookclub.book.domain.BookInfoRepository;
import com.eunhasoo.bookclub.helper.Fixture;
import com.eunhasoo.bookclub.helper.FixtureList;
import com.eunhasoo.bookclub.review.domain.Comment;
import com.eunhasoo.bookclub.review.domain.CommentRepository;
import com.eunhasoo.bookclub.review.domain.Review;
import com.eunhasoo.bookclub.review.domain.ReviewRepository;
import com.eunhasoo.bookclub.review.ui.request.CommentCreate;
import com.eunhasoo.bookclub.review.ui.request.CommentEdit;
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

import static com.eunhasoo.bookclub.helper.Fixture.*;
import static com.eunhasoo.bookclub.helper.Fixture.bookInfo;
import static com.eunhasoo.bookclub.helper.Fixture.review;
import static com.eunhasoo.bookclub.helper.Fixture.userWithEncodedPassword;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CommentControllerTest {

    private static final String COMMENT_API = "/api/comments";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private BookInfoRepository bookInfoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @MockBean
    private TokenProvider tokenProvider;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    @AfterEach
    void clear() {
        commentRepository.deleteAll();
        reviewRepository.deleteAll();
        userRepository.deleteAll();
        bookInfoRepository.deleteAll();
    }

    @Test
    @DisplayName(COMMENT_API + " POST 요청시 리뷰의 댓글을 생성하고 201 응답 코드를 생성한다.")
    void create_comment() throws Exception {
        // given
        BookInfo bookinfo = bookInfoRepository.save(bookInfo());
        User user = userRepository.save(userWithEncodedPassword());
        Review review = reviewRepository.save(review(bookinfo, user));

        CommentCreate commentCreate = new CommentCreate(review.getId(), "댓글");

        when(tokenProvider.validateToken(anyString())).thenReturn(true);
        when(tokenProvider.createToken(anyLong())).thenReturn("token");
        when(userDetailsService.loadUserByUserId(anyLong())).thenReturn(CustomUserDetails.create(user));
        when(tokenProvider.getUserIdFromToken(anyString())).thenReturn(user.getId());

        mockMvc.perform(post(COMMENT_API)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                        .content(om.writeValueAsString(commentCreate))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName(COMMENT_API + "/{commentId} POST 요청시 댓글을 수정하고 200 응답 코드를 생성한다.")
    void edit_comment() throws Exception {
        // given
        BookInfo bookinfo = bookInfoRepository.save(bookInfo());
        User user = userRepository.save(userWithEncodedPassword());
        Review review = reviewRepository.save(review(bookinfo, user));
        Comment comment = commentRepository.save(comment(review, user));

        CommentEdit commentEdit = new CommentEdit("수정된 댓글");

        when(tokenProvider.validateToken(anyString())).thenReturn(true);
        when(tokenProvider.createToken(anyLong())).thenReturn("token");
        when(userDetailsService.loadUserByUserId(anyLong())).thenReturn(CustomUserDetails.create(user));
        when(tokenProvider.getUserIdFromToken(anyString())).thenReturn(user.getId());

        mockMvc.perform(post(COMMENT_API + "/{commentId}", comment.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                        .content(om.writeValueAsString(commentEdit))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName(COMMENT_API + "/{commentId} DELETE 요청시 댓글을 삭제하고 204 응답 코드를 생성한다.")
    void delete_comment() throws Exception {
        // given
        BookInfo bookinfo = bookInfoRepository.save(bookInfo());
        User user = userRepository.save(userWithEncodedPassword());
        Review review = reviewRepository.save(review(bookinfo, user));
        Comment comment = commentRepository.save(comment(review, user));

        when(tokenProvider.validateToken(anyString())).thenReturn(true);
        when(tokenProvider.createToken(anyLong())).thenReturn("token");
        when(userDetailsService.loadUserByUserId(anyLong())).thenReturn(CustomUserDetails.create(user));
        when(tokenProvider.getUserIdFromToken(anyString())).thenReturn(user.getId());

        mockMvc.perform(delete(COMMENT_API + "/{commentId}", comment.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isNoContent());
    }
}
