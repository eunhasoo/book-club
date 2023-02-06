package com.eunhasoo.bookclub.review.domain;

import com.eunhasoo.bookclub.review.ui.request.CommentSearch;

import java.util.List;

public interface CommentRepositoryCustom {

    List<Comment> getList(Long reviewId, CommentSearch commentSearch);
}
