package com.eunhasoo.bookclub.review.domain;

import com.eunhasoo.bookclub.review.ui.request.ReviewSearch;

import java.util.List;

public interface ReviewRepositoryCustom {

    List<Review> getList(ReviewSearch reviewSearch);
}
