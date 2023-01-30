package com.eunhasoo.bookclub.review.domain;

import com.eunhasoo.bookclub.book.domain.BookInfo;
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
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Getter
@Entity
public class Review extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    private BookInfo bookInfo;

    @Column(nullable = false, length = 50)
    private String title;

    @Column(nullable = false)
    @Lob
    private String content;

    @Column(nullable = false)
    @Min(value = 1)
    @Max(value = 5)
    private int score;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id")
    private User reviewer;

    protected Review() {
    }

    @Builder
    public Review(BookInfo bookInfo,
                  String title,
                  String content,
                  User reviewer,
                  int score) {
        this.bookInfo = bookInfo;
        this.title = title;
        this.reviewer = reviewer;
        this.content = content;
        this.score = score;
    }
}
