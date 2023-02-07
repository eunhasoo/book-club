package com.eunhasoo.bookclub.book.domain;

import com.eunhasoo.bookclub.common.BaseTime;
import com.eunhasoo.bookclub.exception.book.BookshelfAccessFailureException;
import com.eunhasoo.bookclub.user.domain.User;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Getter
@Entity
public class Bookshelf extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false)
    private String name;

    @Column(nullable = false)
    private boolean isOpen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    protected Bookshelf() {
    }

    @Builder
    public Bookshelf(String name, User user, boolean isOpen) {
        this.name = name;
        this.user = user;
        this.isOpen = isOpen;
    }

    public void update(String name, boolean isOpen) {
        this.name = name;
        this.isOpen = isOpen;
    }

    public void checkAccessibility(Long userId) {
        boolean isOwner = user.getId().equals(userId);
        if (!isOwner && !isOpen) {
            throw new BookshelfAccessFailureException();
        }
    }
}
