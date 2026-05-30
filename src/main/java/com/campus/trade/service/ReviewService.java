package com.campus.trade.service;

import com.campus.trade.entity.Review;
import java.util.List;
import java.util.Map;

public interface ReviewService {
    Review submitReview(Review review, Integer userId);
    Review getReviewById(Integer id);
    Review getReviewByOrderAndType(Integer orderId, Integer type);
    List<Review> getReviewsByGoodsId(Integer goodsId);
    List<Review> getReviewsByTargetId(Integer targetId);
    List<Review> getReviewsByReviewerId(Integer reviewerId);
    Map<String, Object> getUserRatingStats(Integer userId);
}