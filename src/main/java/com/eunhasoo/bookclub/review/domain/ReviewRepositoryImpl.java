package com.eunhasoo.bookclub.review.domain;

import com.eunhasoo.bookclub.book.domain.BookType;
import com.eunhasoo.bookclub.book.domain.Genre;
import com.eunhasoo.bookclub.review.ui.request.ReviewSearch;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.List;

import static com.eunhasoo.bookclub.book.domain.QBookInfo.bookInfo;
import static com.eunhasoo.bookclub.review.domain.QReview.*;
import static com.eunhasoo.bookclub.user.domain.QUser.user;

public class ReviewRepositoryImpl implements ReviewRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public ReviewRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public List<Review> getList(ReviewSearch reviewSearch) {
        return jpaQueryFactory
                .selectFrom(review)
                .leftJoin(review.bookInfo, bookInfo).fetchJoin()
                .leftJoin(review.reviewer, user).fetchJoin()
                .where(eqBookType(reviewSearch.getBookType()),
                        eqGenre(reviewSearch.getGenre()))
                .limit(reviewSearch.getSize())
                .offset(reviewSearch.getOffset())
                .orderBy(review.id.desc())
                .fetch();
    }

    private BooleanExpression eqBookType(BookType bookType) {
        if (bookType == null) return null;
        return bookInfo.bookType.eq(bookType);
    }

    private BooleanExpression eqGenre(Genre genre) {
        if (genre == null) return null;
        return bookInfo.genre.eq(genre);
    }
}
