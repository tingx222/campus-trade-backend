package com.campus.trade.service;

import com.campus.trade.entity.Order;

import java.util.List;

public interface OrderService {

    /** 买家下单 */
    Order createOrder(Long buyerId, Long goodsId, String address);

    /** 买家付款 */
    void payOrder(Long buyerId, Long orderId);

    /** 卖家发货 */
    void shipOrder(Long sellerId, Long orderId);

    /** 买家确认收货 */
    void confirmOrder(Long buyerId, Long orderId);

    /** 取消订单 */
    void cancelOrder(Long userId, Long orderId);

    /** 查询订单详情 */
    Order getOrderDetail(Long orderId);

    /** 买家订单列表 */
    List<Order> getBuyerOrders(Long buyerId);

    /** 卖家订单列表 */
    List<Order> getSellerOrders(Long sellerId);
}