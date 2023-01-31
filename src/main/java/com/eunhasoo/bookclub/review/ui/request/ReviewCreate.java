package com.eunhasoo.bookclub.review.ui.request;

import lombok.Getter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
public class ReviewCreate {

    @NotNull(message = "리뷰할 책이 선택되지 않았습니다.")
    private Long bookInfoId;

    @NotBlank(message = "리뷰 제목을 작성해주세요.")
    @Size(max = 30, message = "리뷰 제목은 30자 이내로 작성해주세요.")
    private String title;

    @NotBlank(message = "리뷰 내용을 작성해주세요.")
    private String content;

    @NotNull(message = "평점을 선택해주세요.")
    @Min(value = 1, message = "평점은 별 1개 이상이어야 합니다.")
    @Max(value = 5, message = "평점은 별 5개를 초과할 수 없습니다.")
    private int score;

    private ReviewCreate() {
    }

    public ReviewCreate(Long bookInfoId, String title, String content, int score) {
        this.bookInfoId = bookInfoId;
        this.title = title;
        this.content = content;
        this.score = score;
    }
}
