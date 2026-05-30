package com.campus.trade.mapper;

import com.campus.trade.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderMapper {

    int insert(Order order);

    int update(Order order);

    Order findById(@Param("id") Long id);

    Order findByOrderNo(@Param("orderNo") String orderNo);

    /** 买家订单列表 */
    List<Order> findByBuyerId(@Param("buyerId") Long buyerId);

    /** 卖家订单列表 */
    List<Order> findBySellerId(@Param("sellerId") Long sellerId);
}