package com.eunhasoo.bookclub.review.domain;

import com.eunhasoo.bookclub.common.BaseTime;
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
public class Comment extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 3000)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    protected Comment() {
    }

    @Builder
    public Comment(String content, Review review, User user) {
        this.content = content;
        this.review = review;
        this.user = user;
    }

    public void edit(String content) {
        this.content = content;
    }
}
