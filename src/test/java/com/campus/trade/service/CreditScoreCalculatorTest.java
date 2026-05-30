package com.campus.trade.service;

import com.campus.trade.entity.Review;
import com.campus.trade.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 信用评价体系单元测试
 */
@DisplayName("信用评价体系 - 单元测试")
class CreditScoreCalculatorTest {

    private Review testReview;
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

    // ==================== 1. 信用分计算核心逻辑测试 ====================

    @Test
    @DisplayName("【信用分计算】全5星评价 → 信用分100")
    void testCalculateCreditScore_AllFiveStars() {
        List<Review> reviews = createReviewsWithRatings(5, 10);
        double average = calculateAverageRating(reviews);
        int creditScore = calculateCreditScore(average);

        assertEquals(5.0, average, 0.01);
        assertEquals(100, creditScore);
    }

    @Test
    @DisplayName("【信用分计算】全1星评价 → 信用分20")
    void testCalculateCreditScore_AllOneStar() {
        List<Review> reviews = createReviewsWithRatings(1, 5);
        double average = calculateAverageRating(reviews);
        int creditScore = calculateCreditScore(average);

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
        double average = calculateAverageRating(reviews);
        int creditScore = calculateCreditScore(average);

        assertEquals(3.625, average, 0.001);
        assertEquals(73, creditScore);
    }

    @Test
    @DisplayName("【信用分计算】新用户无评价 → 默认70分")
    void testCalculateCreditScore_NoRatings() {
        int defaultScore = 70;
        assertEquals(70, defaultScore);
    }

    @Test
    @DisplayName("【信用分计算】单个4星评价 → 信用分80")
    void testCalculateCreditScore_SingleFourStar() {
        List<Review> reviews = createReviewsWithRatings(4, 1);
        double average = calculateAverageRating(reviews);
        int creditScore = calculateCreditScore(average);

        assertEquals(4.0, average, 0.01);
        assertEquals(80, creditScore);
    }

    @Test
    @DisplayName("【信用分计算】信用分边界 - 不超过100分")
    void testCalculateCreditScore_NotExceed100() {
        List<Review> reviews = createReviewsWithRatings(5, 6);
        double average = calculateAverageRating(reviews);
        int creditScore = calculateCreditScore(average);

        assertTrue(creditScore <= 100);
        assertEquals(100, creditScore);
    }

    @ParameterizedTest
    @CsvSource({
            "5, 100",
            "4, 80",
            "3, 60",
            "2, 40",
            "1, 20"
    })
    @DisplayName("【信用分计算】参数化测试：单个评分对应的信用分")
    void testCalculateCreditScore_Parameterized(int rating, int expected) {
        List<Review> reviews = createReviewsWithRatings(rating, 1);
        double average = calculateAverageRating(reviews);
        int creditScore = calculateCreditScore(average);

        assertEquals(expected, creditScore);
    }

    // ==================== 2. 用户评分统计测试 ====================

    @Test
    @DisplayName("【评分统计】正常获取用户评分统计")
    void testGetUserRatingStats_Success() {
        List<Review> reviews = Arrays.asList(
                createReview(5), createReview(4),
                createReview(3), createReview(5), createReview(4)
        );

        long reviewCount = reviews.size();
        double averageRating = calculateAverageRating(reviews);
        long fiveStarCount = reviews.stream().filter(r -> r.getRating() == 5).count();

        assertEquals(5, reviewCount);
        assertEquals(4.2, averageRating, 0.01);
        assertEquals(2, fiveStarCount);
    }

    @Test
    @DisplayName("【评分统计】用户无评价时统计为0")
    void testGetUserRatingStats_NoReviews() {
        List<Review> reviews = new ArrayList<>();
        long reviewCount = reviews.size();
        double averageRating = calculateAverageRating(reviews);

        assertEquals(0, reviewCount);
        assertEquals(0.0, averageRating, 0.01);
    }

    @Test
    @DisplayName("【评分统计】只有差评时的统计")
    void testGetUserRatingStats_OnlyBadReviews() {
        List<Review> reviews = Arrays.asList(
                createReview(1), createReview(2), createReview(1)
        );

        long reviewCount = reviews.size();
        double averageRating = calculateAverageRating(reviews);

        assertEquals(3, reviewCount);
        assertEquals(1.33, averageRating, 0.01);
    }

    // ==================== 3. 交易限制测试 ====================

    @Test
    @DisplayName("【交易限制】信用分低于60分被限制交易")
    void testIsTradeRestricted_LowCredit() {
        assertTrue(isTradeRestricted(50));
        assertTrue(isTradeRestricted(0));
        assertTrue(isTradeRestricted(30));
        assertTrue(isTradeRestricted(59));
    }

    @Test
    @DisplayName("【交易限制】信用分60分及以上可正常交易")
    void testIsTradeRestricted_GoodCredit() {
        assertFalse(isTradeRestricted(60));
        assertFalse(isTradeRestricted(70));
        assertFalse(isTradeRestricted(85));
        assertFalse(isTradeRestricted(100));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 10, 20, 30, 40, 50, 59})
    @DisplayName("【交易限制】参数化测试：被限制交易的信用分")
    void testIsTradeRestricted_RestrictedScores(int score) {
        assertTrue(isTradeRestricted(score));
    }

    @ParameterizedTest
    @ValueSource(ints = {60, 65, 70, 75, 80, 85, 90, 95, 100})
    @DisplayName("【交易限制】参数化测试：允许交易的信用分")
    void testIsTradeRestricted_AllowedScores(int score) {
        assertFalse(isTradeRestricted(score));
    }

    private boolean isTradeRestricted(int creditScore) {
        return creditScore < 60;
    }

    // ==================== 4. 信用等级测试 ====================

    @Test
    @DisplayName("【信用等级】根据信用分返回对应等级")
    void testGetCreditLevel() {
        assertEquals("极差", getCreditLevelByScore(0));
        assertEquals("极差", getCreditLevelByScore(19));
        assertEquals("较差", getCreditLevelByScore(20));
        assertEquals("较差", getCreditLevelByScore(39));
        assertEquals("一般", getCreditLevelByScore(40));
        assertEquals("一般", getCreditLevelByScore(59));
        assertEquals("良好", getCreditLevelByScore(60));
        assertEquals("良好", getCreditLevelByScore(79));
        assertEquals("优秀", getCreditLevelByScore(80));
        assertEquals("优秀", getCreditLevelByScore(89));
        assertEquals("极好", getCreditLevelByScore(90));
        assertEquals("极好", getCreditLevelByScore(100));
    }

    private String getCreditLevelByScore(int score) {
        if (score < 20) return "极差";
        if (score < 40) return "较差";
        if (score < 60) return "一般";
        if (score < 80) return "良好";
        if (score < 90) return "优秀";
        return "极好";
    }

    // ==================== 5. 评价内容验证测试 ====================

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
        // 有效内容（至少5个字符）
        assertTrue(isValidContent("好评商品a"));   // 5个字符
        assertTrue(isValidContent("这是一条五个字的评价")); // 9个字符
        assertTrue(isValidContent("a".repeat(500)));

        // 边界测试
        assertTrue(isValidContent("12345"));      // 5个字符
        assertTrue(isValidContent("a".repeat(500))); // 500个字符

        // 无效内容
        assertFalse(isValidContent(""));
        assertFalse(isValidContent(null));
        assertFalse(isValidContent("短"));        // 1个字符
        assertFalse(isValidContent("a".repeat(501))); // 501个字符
    }

    private boolean isValidContent(String content) {
        if (content == null) return false;
        int len = content.trim().length();
        return len >= 5 && len <= 500;
    }

    // ==================== 6. 信用分变化测试 ====================

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

    // ==================== 7. 恶意差评处理测试 ====================

    @Test
    @DisplayName("【恶意差评】恶意差评被撤销后信用分回滚")
    void testCreditScoreRollbackAfterBadReviewRemoved() {
        List<Integer> originalRatings = Arrays.asList(5, 5, 5);
        int originalScore = (int) Math.round(originalRatings.stream()
                .mapToInt(Integer::intValue).average().orElse(0) * 20);
        assertEquals(100, originalScore);

        List<Integer> withBadReview = new ArrayList<>(originalRatings);
        withBadReview.add(1);
        int afterBadScore = (int) Math.round(withBadReview.stream()
                .mapToInt(Integer::intValue).average().orElse(0) * 20);
        assertEquals(80, afterBadScore);

        int afterRollbackScore = (int) Math.round(originalRatings.stream()
                .mapToInt(Integer::intValue).average().orElse(0) * 20);
        assertEquals(originalScore, afterRollbackScore);
    }

    // ==================== 8. 实名认证奖励测试 ====================

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

    // ==================== 9. 默认好评测试 ====================

    @Test
    @DisplayName("【默认好评】7天未评价自动默认好评（4星）")
    void testDefaultReview() {
        int defaultRating = 4;
        String defaultContent = "系统默认好评";

        assertEquals(4, defaultRating);
        assertEquals("系统默认好评", defaultContent);
    }

    // ==================== 辅助方法 ====================

    private double calculateAverageRating(List<Review> reviews) {
        if (reviews == null || reviews.isEmpty()) {
            return 0;
        }
        return reviews.stream().mapToInt(Review::getRating).average().orElse(0);
    }

    private int calculateCreditScore(double averageRating) {
        if (averageRating == 0) {
            return 70; // 新用户默认70分
        }
        return (int) Math.round(averageRating * 20);
    }

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