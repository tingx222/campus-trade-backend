package com.campus.trade.controller;

import com.campus.trade.common.ResultVO;
import com.campus.trade.entity.Review;
import com.campus.trade.service.ReviewService;
import com.campus.trade.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/review")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/submit")
    public ResultVO submitReview(@RequestBody Review review, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return ResultVO.fail(401, "请先登录");
        }
        token = token.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return ResultVO.fail(401, "登录已过期");
        }
        Long userId = jwtUtil.getUserId(token);
        try {
            Review result = reviewService.submitReview(review, userId.intValue());
            return ResultVO.success(result);
        } catch (RuntimeException e) {
            return ResultVO.fail(400, e.getMessage());
        }
    }

    @GetMapping("/detail")
    public ResultVO getReviewById(@RequestParam Integer id) {
        Review review = reviewService.getReviewById(id);
        if (review == null) {
            return ResultVO.fail(404, "评价不存在");
        }
        return ResultVO.success(review);
    }

    @GetMapping("/check")
    public ResultVO checkReview(@RequestParam Integer orderId, @RequestParam Integer type, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return ResultVO.fail(401, "请先登录");
        }
        token = token.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return ResultVO.fail(401, "登录已过期");
        }
        Review review = reviewService.getReviewByOrderAndType(orderId, type);
        return ResultVO.success(review);
    }

    @GetMapping("/goods")
    public ResultVO getReviewsByGoodsId(@RequestParam Integer goodsId) {
        List<Review> list = reviewService.getReviewsByGoodsId(goodsId);
        return ResultVO.success(list);
    }

    @GetMapping("/target")
    public ResultVO getReviewsByTargetId(@RequestParam Integer targetId) {
        List<Review> list = reviewService.getReviewsByTargetId(targetId);
        return ResultVO.success(list);
    }

    @GetMapping("/my")
    public ResultVO getMyReviews(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return ResultVO.fail(401, "请先登录");
        }
        token = token.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return ResultVO.fail(401, "登录已过期");
        }
        Long userId = jwtUtil.getUserId(token);
        List<Review> list = reviewService.getReviewsByReviewerId(userId.intValue());
        return ResultVO.success(list);
    }

    @GetMapping("/stats")
    public ResultVO getUserRatingStats(@RequestParam Integer userId) {
        Map<String, Object> stats = reviewService.getUserRatingStats(userId);
        return ResultVO.success(stats);
    }
}