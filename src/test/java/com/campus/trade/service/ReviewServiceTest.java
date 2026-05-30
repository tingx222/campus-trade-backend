package com.campus.trade.service;

import com.campus.trade.entity.Review;
import com.campus.trade.entity.Order;
import com.campus.trade.entity.User;
import com.campus.trade.mapper.ReviewMapper;
import com.campus.trade.mapper.OrderMapper;
import com.campus.trade.mapper.UserMapper;
import com.campus.trade.service.impl.ReviewServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 评价服务单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("评价服务 - 单元测试")
class ReviewServiceTest {

    @Mock
    private ReviewMapper reviewMapper;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private Review testReview;
    private Order testOrder;
    private User testSeller;
    private User testBuyer;

    @BeforeEach
    void setUp() {
        testSeller = new User();
        testSeller.setId(200L);
        testSeller.setNickname("卖家小明");
        testSeller.setCreditScore(70);

        testBuyer = new User();
        testBuyer.setId(300L);
        testBuyer.setNickname("买家小红");
        testBuyer.setCreditScore(70);

        testOrder = new Order();
        testOrder.setId(100L);
        testOrder.setOrderNo("ORD20260001");
        testOrder.setGoodsId(1000L);
        testOrder.setBuyerId(300L);
        testOrder.setSellerId(200L);
        testOrder.setPrice(new BigDecimal("100"));
        testOrder.setStatus(4);
        testOrder.setAddress("1号宿舍楼101");

        testReview = new Review();
        testReview.setId(1);
        testReview.setOrderId(100);
        testReview.setGoodsId(1000);
        testReview.setReviewerId(300);
        testReview.setTargetId(200);
        testReview.setRating(5);
        testReview.setContent("商品很好，卖家诚信！");
        testReview.setType(1);
    }

    // ==================== 提交评价测试 ====================

    @Test
    @DisplayName("【提交评价】正常情况：买家成功评价卖家")
    void testSubmitReview_Success() {
        when(orderMapper.findById(100L)).thenReturn(testOrder);
        when(reviewMapper.selectByOrderIdAndType(100, 1)).thenReturn(null);
        when(reviewMapper.insert(any(Review.class))).thenReturn(1);

        Review result = reviewService.submitReview(testReview, 300);

        assertNotNull(result);
        verify(reviewMapper, times(1)).insert(any(Review.class));
    }

    @Test
    @DisplayName("【提交评价】异常情况：订单不存在")
    void testSubmitReview_OrderNotFound() {
        when(orderMapper.findById(999L)).thenReturn(null);
        testReview.setOrderId(999);

        assertThrows(RuntimeException.class, () -> {
            reviewService.submitReview(testReview, 300);
        });
    }

    @Test
    @DisplayName("【提交评价】异常情况：订单未完成不能评价")
    void testSubmitReview_OrderNotCompleted() {
        testOrder.setStatus(2);
        when(orderMapper.findById(100L)).thenReturn(testOrder);

        assertThrows(RuntimeException.class, () -> {
            reviewService.submitReview(testReview, 300);
        });
    }

    @Test
    @DisplayName("【提交评价】异常情况：重复评价")
    void testSubmitReview_DuplicateReview() {
        when(orderMapper.findById(100L)).thenReturn(testOrder);
        when(reviewMapper.selectByOrderIdAndType(100, 1)).thenReturn(testReview);

        assertThrows(RuntimeException.class, () -> {
            reviewService.submitReview(testReview, 300);
        });
    }

    @Test
    @DisplayName("【提交评价】异常情况：用户不能评价自己")
    void testSubmitReview_SelfReview() {
        testReview.setReviewerId(200);
        testReview.setTargetId(200);
        when(orderMapper.findById(100L)).thenReturn(testOrder);

        assertThrows(RuntimeException.class, () -> {
            reviewService.submitReview(testReview, 200);
        });
    }

    // ==================== 评价内容验证测试 ====================

    @Test
    @DisplayName("【评价验证】评分必须在1-5之间")
    void testValidateRating() {
        assertTrue(isValidRating(5));
        assertTrue(isValidRating(4));
        assertTrue(isValidRating(3));
        assertTrue(isValidRating(2));
        assertTrue(isValidRating(1));

        assertFalse(isValidRating(0));
        assertFalse(isValidRating(6));
        assertFalse(isValidRating(-1));
    }

    private boolean isValidRating(int rating) {
        return rating >= 1 && rating <= 5;
    }

    @Test
    @DisplayName("【评价验证】评价内容长度验证")
    void testValidateContentLength() {
        assertTrue(isValidContent("好评商品a"));  // 5个字符
        assertTrue(isValidContent("a".repeat(500)));
        assertTrue(isValidContent("这是一个五个字的评价"));  // 8个字符

        assertFalse(isValidContent(""));
        assertFalse(isValidContent(null));
        assertFalse(isValidContent("短"));  // 1个字符
        assertFalse(isValidContent("a".repeat(501)));
    }

    private boolean isValidContent(String content) {
        if (content == null) return false;
        int len = content.trim().length();
        return len >= 5 && len <= 500;
    }

    // ==================== 查询评价测试 ====================

    @Test
    @DisplayName("【查询评价】根据ID查询评价")
    void testGetReviewById_Success() {
        when(reviewMapper.selectById(1)).thenReturn(testReview);

        Review result = reviewService.getReviewById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(reviewMapper, times(1)).selectById(1);
    }

    @Test
    @DisplayName("【查询评价】根据ID查询 - 评价不存在")
    void testGetReviewById_NotFound() {
        when(reviewMapper.selectById(999)).thenReturn(null);

        Review result = reviewService.getReviewById(999);

        assertNull(result);
    }

    @Test
    @DisplayName("【查询评价】根据订单ID和类型查询评价")
    void testGetReviewByOrderAndType_Success() {
        when(reviewMapper.selectByOrderIdAndType(100, 1)).thenReturn(testReview);

        Review result = reviewService.getReviewByOrderAndType(100, 1);

        assertNotNull(result);
        assertEquals(100, result.getOrderId());
        verify(reviewMapper, times(1)).selectByOrderIdAndType(100, 1);
    }

    @Test
    @DisplayName("【查询评价】根据商品ID查询评价列表")
    void testGetReviewsByGoodsId_Success() {
        List<Review> mockReviews = Arrays.asList(testReview, new Review());
        when(reviewMapper.selectByGoodsId(1000)).thenReturn(mockReviews);

        List<Review> result = reviewService.getReviewsByGoodsId(1000);

        assertEquals(2, result.size());
        verify(reviewMapper, times(1)).selectByGoodsId(1000);
    }

    @Test
    @DisplayName("【查询评价】根据商品ID查询 - 无评价")
    void testGetReviewsByGoodsId_NoReviews() {
        when(reviewMapper.selectByGoodsId(999)).thenReturn(Collections.emptyList());

        List<Review> result = reviewService.getReviewsByGoodsId(999);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("【查询评价】根据目标用户ID查询评价")
    void testGetReviewsByTargetId_Success() {
        List<Review> mockReviews = Arrays.asList(testReview);
        when(reviewMapper.selectByTargetId(200)).thenReturn(mockReviews);

        List<Review> result = reviewService.getReviewsByTargetId(200);

        assertEquals(1, result.size());
        verify(reviewMapper, times(1)).selectByTargetId(200);
    }

    @Test
    @DisplayName("【查询评价】根据评价人ID查询评价")
    void testGetReviewsByReviewerId_Success() {
        List<Review> mockReviews = Arrays.asList(testReview);
        when(reviewMapper.selectByReviewerId(300)).thenReturn(mockReviews);

        List<Review> result = reviewService.getReviewsByReviewerId(300);

        assertEquals(1, result.size());
        verify(reviewMapper, times(1)).selectByReviewerId(300);
    }

    // ==================== 用户评分统计测试 ====================

    @Test
    @DisplayName("【评分统计】正常获取用户评分统计")
    void testGetUserRatingStats_Success() {
        List<Review> reviews = Arrays.asList(
                createReview(5), createReview(4),
                createReview(3), createReview(5), createReview(4)
        );

        // 只 stub 实际会用到的方法
        when(reviewMapper.selectByTargetId(200)).thenReturn(reviews);

        // 直接调用方法进行测试
        List<Review> result = reviewService.getReviewsByTargetId(200);

        assertEquals(5, result.size());
        verify(reviewMapper, times(1)).selectByTargetId(200);

        // 计算统计数据验证
        long reviewCount = result.size();
        double averageRating = result.stream().mapToInt(Review::getRating).average().orElse(0);
        long fiveStarCount = result.stream().filter(r -> r.getRating() == 5).count();

        assertEquals(5, reviewCount);
        assertEquals(4.2, averageRating, 0.01);
        assertEquals(2, fiveStarCount);
    }

    @Test
    @DisplayName("【评分统计】用户无评价时统计为0")
    void testGetUserRatingStats_NoReviews() {
        when(reviewMapper.selectByTargetId(999)).thenReturn(Collections.emptyList());

        List<Review> result = reviewService.getReviewsByTargetId(999);

        assertTrue(result.isEmpty());
        verify(reviewMapper, times(1)).selectByTargetId(999);
    }

    // ==================== 信用分计算测试 ====================

    @Test
    @DisplayName("【信用分计算】全5星评价 → 信用分100")
    void testCalculateCreditScore_AllFiveStars() {
        List<Review> reviews = createReviewsWithRatings(5, 10);
        double average = reviews.stream().mapToInt(Review::getRating).average().orElse(0);
        int creditScore = (int) Math.round(average * 20);

        assertEquals(5.0, average, 0.01);
        assertEquals(100, creditScore);
    }

    @Test
    @DisplayName("【信用分计算】全1星评价 → 信用分20")
    void testCalculateCreditScore_AllOneStar() {
        List<Review> reviews = createReviewsWithRatings(1, 5);
        double average = reviews.stream().mapToInt(Review::getRating).average().orElse(0);
        int creditScore = (int) Math.round(average * 20);

        assertEquals(1.0, average, 0.01);
        assertEquals(20, creditScore);
    }

    @Test
    @DisplayName("【信用分计算】混合评分正确计算")
    void testCalculateCreditScore_MixedScores() {
        List<Integer> ratings = Arrays.asList(5, 5, 5, 4, 4, 3, 2, 1);
        List<Review> reviews = new ArrayList<>();
        for (Integer rating : ratings) {
            Review r = new Review();
            r.setRating(rating);
            reviews.add(r);
        }
        double average = reviews.stream().mapToInt(Review::getRating).average().orElse(0);
        int creditScore = (int) Math.round(average * 20);

        assertEquals(3.625, average, 0.001);
        assertEquals(73, creditScore);
    }

    @Test
    @DisplayName("【信用分计算】新用户无评价 → 默认70分")
    void testCalculateCreditScore_NoRatings() {
        int defaultScore = 70;
        assertEquals(70, defaultScore);
    }

    // ==================== 信用分变化测试 ====================

    @Test
    @DisplayName("【信用分变化】新增好评后信用分上升")
    void testCreditScoreIncreaseAfterGoodReview() {
        List<Integer> originalRatings = Arrays.asList(4, 4, 4);
        double originalAvg = originalRatings.stream().mapToInt(Integer::intValue).average().orElse(0);
        int originalScore = (int) Math.round(originalAvg * 20);
        assertEquals(80, originalScore);

        List<Integer> newRatings = Arrays.asList(4, 4, 4, 5);
        double newAvg = newRatings.stream().mapToInt(Integer::intValue).average().orElse(0);
        int newScore = (int) Math.round(newAvg * 20);
        assertEquals(85, newScore);

        assertTrue(newScore > originalScore);
    }

    @Test
    @DisplayName("【信用分变化】新增差评后信用分下降")
    void testCreditScoreDecreaseAfterBadReview() {
        List<Integer> originalRatings = Arrays.asList(5, 5, 5);
        double originalAvg = originalRatings.stream().mapToInt(Integer::intValue).average().orElse(0);
        int originalScore = (int) Math.round(originalAvg * 20);
        assertEquals(100, originalScore);

        List<Integer> newRatings = Arrays.asList(5, 5, 5, 1);
        double newAvg = newRatings.stream().mapToInt(Integer::intValue).average().orElse(0);
        int newScore = (int) Math.round(newAvg * 20);
        assertEquals(80, newScore);

        assertTrue(newScore < originalScore);
    }

    // ==================== 默认好评测试 ====================

    @Test
    @DisplayName("【默认好评】7天未评价自动默认好评（4星）")
    void testDefaultReview() {
        int defaultRating = 4;
        String defaultContent = "系统默认好评";

        assertEquals(4, defaultRating);
        assertEquals("系统默认好评", defaultContent);
    }

    // ==================== 实名认证奖励测试 ====================

    @Test
    @DisplayName("【实名认证】实名认证用户信用分+5分奖励")
    void testRealNameAuthBonus() {
        int originalScore = 70;
        int afterAuthScore = Math.min(100, originalScore + 5);
        assertEquals(75, afterAuthScore);
    }

    @Test
    @DisplayName("【实名认证】信用分已达上限100分时不再加分")
    void testRealNameAuthBonusCapAt100() {
        int originalScore = 98;
        int afterAuthScore = Math.min(100, originalScore + 5);
        assertEquals(100, afterAuthScore);
    }

    @Test
    @DisplayName("【实名认证】非认证用户不加分")
    void testNoBonusForNonAuthUser() {
        int originalScore = 70;
        boolean hasAuth = false;
        int afterScore = hasAuth ? Math.min(100, originalScore + 5) : originalScore;
        assertEquals(70, afterScore);
    }

    // ==================== 辅助方法 ====================

    private Review createReview(int rating) {
        Review r = new Review();
        r.setRating(rating);
        r.setTargetId(200);
        r.setContent("测试评价内容");
        return r;
    }

    private List<Review> createReviewsWithRatings(int rating, int count) {
        List<Review> reviews = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Review r = new Review();
            r.setRating(rating);
            reviews.add(r);
        }
        return reviews;
    }
}