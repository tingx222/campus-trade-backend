package com.campus.trade.mapper;

import com.campus.trade.entity.Review;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ReviewMapper {
    int insert(Review review);
    Review selectById(Integer id);
    Review selectByOrderIdAndType(@Param("orderId") Integer orderId, @Param("type") Integer type);
    List<Review> selectByGoodsId(Integer goodsId);
    List<Review> selectByTargetId(Integer targetId);
    List<Review> selectByReviewerId(Integer reviewerId);
    Double selectAverageRatingByTargetId(Integer targetId);
    int selectReviewCountByTargetId(Integer targetId);
}