package com.eunhasoo.bookclub.book.domain;

import com.eunhasoo.bookclub.book.ui.request.BookSearch;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.List;

import static com.eunhasoo.bookclub.book.domain.QBook.*;

public class BookRepositoryImpl implements BookRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public BookRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public List<Book> getList(Long bookshelfId, BookSearch bookSearch) {
        return jpaQueryFactory
                .selectFrom(book)
                .leftJoin(book.bookInfo).fetchJoin()
                .where(eqBookshelf(bookshelfId))
                .limit(bookSearch.getSize())
                .offset(bookSearch.getOffset())
                .orderBy(book.id.desc())
                .fetch();
    }

    private BooleanExpression eqBookshelf(Long bookshelfId) {
        return book.bookshelf.id.eq(bookshelfId);
    }
}
