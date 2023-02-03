package com.eunhasoo.bookclub.review.application;

import com.eunhasoo.bookclub.review.domain.Comment;
import com.eunhasoo.bookclub.review.domain.CommentRepository;
import com.eunhasoo.bookclub.review.domain.Review;
import com.eunhasoo.bookclub.review.domain.ReviewRepository;
import com.eunhasoo.bookclub.review.ui.request.CommentCreate;
import com.eunhasoo.bookclub.review.ui.request.CommentSearch;
import com.eunhasoo.bookclub.user.domain.User;
import com.eunhasoo.bookclub.user.domain.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@Service
public class CommentService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    public CommentService(ReviewRepository reviewRepository, UserRepository userRepository, CommentRepository commentRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
    }

    public List<Comment> getComments(CommentSearch commentSearch) {
        return commentRepository.getList(commentSearch);
    }

    @Transactional
    public void create(Long userId, CommentCreate commentCreate) {
        Review review = reviewRepository.getById(commentCreate.getReviewId());
        User user = userRepository.getById(userId);

        Comment comment = Comment.builder()
                .review(review)
                .user(user)
                .content(commentCreate.getContent())
                .build();

        commentRepository.save(comment);
    }
}
