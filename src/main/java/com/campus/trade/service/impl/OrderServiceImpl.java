package com.campus.trade.service.impl;

import com.campus.trade.entity.Goods;
import com.campus.trade.entity.Order;
import com.campus.trade.mapper.GoodsMapper;
import com.campus.trade.mapper.OrderMapper;
import com.campus.trade.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final GoodsMapper goodsMapper;

    public OrderServiceImpl(OrderMapper orderMapper, GoodsMapper goodsMapper) {
        this.orderMapper = orderMapper;
        this.goodsMapper = goodsMapper;
    }

    @Override
    @Transactional
    public Order createOrder(Long buyerId, Long goodsId, String address) {
        // 查询商品
        Goods goods = goodsMapper.findById(goodsId);
        if (goods == null) throw new RuntimeException("商品不存在");
        if (goods.getStatus() != 1) throw new RuntimeException("商品已下架或已售出");
        if (goods.getUserId().equals(buyerId)) throw new RuntimeException("不能购买自己的商品");

        // 生成订单号
        String orderNo = "ORD" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + ThreadLocalRandom.current().nextInt(1000, 9999);

        // 创建订单
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setGoodsId(goodsId);
        order.setBuyerId(buyerId);
        order.setSellerId(goods.getUserId());
        order.setPrice(goods.getPrice());
        order.setAddress(address);
        order.setStatus(1); // 待付款
        order.setCreateTime(LocalDateTime.now());

        orderMapper.insert(order);

        // 商品标记为售出
        Goods updateGoods = new Goods();
        updateGoods.setId(goodsId);
        updateGoods.setStatus(3); // 售出
        goodsMapper.update(updateGoods);

        return order;
    }

    @Override
    @Transactional
    public void payOrder(Long buyerId, Long orderId) {
        Order order = orderMapper.findById(orderId);
        if (order == null) throw new RuntimeException("订单不存在");
        if (!order.getBuyerId().equals(buyerId)) throw new RuntimeException("无权操作此订单");
        if (order.getStatus() != 1) throw new RuntimeException("订单状态不允许付款");

        Order update = new Order();
        update.setId(orderId);
        update.setStatus(2); // 待发货
        orderMapper.update(update);
    }

    @Override
    @Transactional
    public void shipOrder(Long sellerId, Long orderId) {
        Order order = orderMapper.findById(orderId);
        if (order == null) throw new RuntimeException("订单不存在");
        if (!order.getSellerId().equals(sellerId)) throw new RuntimeException("无权操作此订单");
        if (order.getStatus() != 2) throw new RuntimeException("订单状态不允许发货");

        Order update = new Order();
        update.setId(orderId);
        update.setStatus(3); // 待收货
        orderMapper.update(update);
    }

    @Override
    @Transactional
    public void confirmOrder(Long buyerId, Long orderId) {
        Order order = orderMapper.findById(orderId);
        if (order == null) throw new RuntimeException("订单不存在");
        if (!order.getBuyerId().equals(buyerId)) throw new RuntimeException("无权操作此订单");
        if (order.getStatus() != 3) throw new RuntimeException("订单状态不允许确认收货");

        Order update = new Order();
        update.setId(orderId);
        update.setStatus(4); // 已完成
        orderMapper.update(update);
    }

    @Override
    @Transactional
    public void cancelOrder(Long userId, Long orderId) {
        Order order = orderMapper.findById(orderId);
        if (order == null) throw new RuntimeException("订单不存在");
        if (!order.getBuyerId().equals(userId) && !order.getSellerId().equals(userId)) {
            throw new RuntimeException("无权操作此订单");
        }
        if (order.getStatus() == 4) throw new RuntimeException("订单已完成，无法取消");

        // 取消订单
        Order update = new Order();
        update.setId(orderId);
        update.setStatus(5); // 已取消
        orderMapper.update(update);

        // 如果是待付款状态取消，商品恢复上架
        if (order.getStatus() == 1) {
            Goods goodsUpdate = new Goods();
            goodsUpdate.setId(order.getGoodsId());
            goodsUpdate.setStatus(1); // 恢复上架
            goodsMapper.update(goodsUpdate);
        }
    }

    @Override
    public Order getOrderDetail(Long orderId) {
        Order order = orderMapper.findById(orderId);
        if (order == null) throw new RuntimeException("订单不存在");
        return order;
    }

    @Override
    public List<Order> getBuyerOrders(Long buyerId) {
        return orderMapper.findByBuyerId(buyerId);
    }

    @Override
    public List<Order> getSellerOrders(Long sellerId) {
        return orderMapper.findBySellerId(sellerId);
    }
}