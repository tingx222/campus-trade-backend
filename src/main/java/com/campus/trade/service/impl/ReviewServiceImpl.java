package com.campus.trade.service.impl;

import com.campus.trade.entity.Order;
import com.campus.trade.entity.Review;
import com.campus.trade.mapper.OrderMapper;
import com.campus.trade.mapper.ReviewMapper;
import com.campus.trade.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewMapper reviewMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Override
    @Transactional
    public Review submitReview(Review review, Integer userId) {
        Order order = orderMapper.findById(Long.valueOf(review.getOrderId()));
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getStatus() != 4) {
            throw new RuntimeException("订单未完成，无法评价");
        }
        if (review.getType() == 1) {
            if (!order.getBuyerId().equals(Long.valueOf(userId))) {
                throw new RuntimeException("无权评价");
            }
            review.setReviewerId(order.getBuyerId().intValue());
            review.setTargetId(order.getSellerId().intValue());
        } else if (review.getType() == 2) {
            if (!order.getSellerId().equals(Long.valueOf(userId))) {
                throw new RuntimeException("无权评价");
            }
            review.setReviewerId(order.getSellerId().intValue());
            review.setTargetId(order.getBuyerId().intValue());
        } else {
            throw new RuntimeException("评价类型错误");
        }
        review.setGoodsId(order.getGoodsId().intValue());
        Review existing = reviewMapper.selectByOrderIdAndType(review.getOrderId(), review.getType());
        if (existing != null) {
            throw new RuntimeException("已评价，不能重复评价");
        }
        if (review.getRating() == null || review.getRating() < 1 || review.getRating() > 5) {
            throw new RuntimeException("评分必须在1-5之间");
        }
        reviewMapper.insert(review);
        return review;
    }

    @Override
    public Review getReviewById(Integer id) {
        return reviewMapper.selectById(id);
    }

    @Override
    public Review getReviewByOrderAndType(Integer orderId, Integer type) {
        return reviewMapper.selectByOrderIdAndType(orderId, type);
    }

    @Override
    public List<Review> getReviewsByGoodsId(Integer goodsId) {
        return reviewMapper.selectByGoodsId(goodsId);
    }

    @Override
    public List<Review> getReviewsByTargetId(Integer targetId) {
        return reviewMapper.selectByTargetId(targetId);
    }

    @Override
    public List<Review> getReviewsByReviewerId(Integer reviewerId) {
        return reviewMapper.selectByReviewerId(reviewerId);
    }

    @Override
    public Map<String, Object> getUserRatingStats(Integer userId) {
        Map<String, Object> stats = new HashMap<>();
        Double avgRating = reviewMapper.selectAverageRatingByTargetId(userId);
        Integer count = reviewMapper.selectReviewCountByTargetId(userId);
        stats.put("averageRating", avgRating != null ? String.format("%.1f", avgRating) : "0.0");
        stats.put("reviewCount", count);
        return stats;
    }
}