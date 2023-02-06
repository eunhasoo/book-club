package com.eunhasoo.bookclub.review.ui.response;

import com.eunhasoo.bookclub.review.domain.Comment;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentListResponse {
    private Long commentId;
    private String content;
    private Long commenterId;
    private String commenter;
    private LocalDateTime updatedDate;

    public CommentListResponse(Comment comment) {
        this.commentId = comment.getId();
        this.content = comment.getContent();
        this.commenterId = comment.getUser().getId();
        this.commenter = comment.getUser().getNickname();
        this.updatedDate = comment.getUpdatedDate();
    }
}
