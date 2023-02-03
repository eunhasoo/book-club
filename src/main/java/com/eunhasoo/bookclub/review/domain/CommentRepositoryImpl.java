package com.eunhasoo.bookclub.review.domain;

import com.eunhasoo.bookclub.review.ui.request.CommentSearch;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.List;

import static com.eunhasoo.bookclub.review.domain.QComment.*;
import static com.eunhasoo.bookclub.user.domain.QUser.user;

public class CommentRepositoryImpl implements CommentRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public CommentRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public List<Comment> getList(CommentSearch commentSearch) {
        return jpaQueryFactory.selectFrom(comment)
                .leftJoin(comment.user, user).fetchJoin()
                .where(comment.review.id.eq(commentSearch.getReviewId()))
                .limit(commentSearch.getSize())
                .offset(commentSearch.getOffset())
                .orderBy(comment.id.asc())
                .fetch();
    }
}
