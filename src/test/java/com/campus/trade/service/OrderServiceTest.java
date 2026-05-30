package com.campus.trade.service;

import com.campus.trade.entity.Goods;
import com.campus.trade.entity.Order;
import com.campus.trade.entity.User;
import com.campus.trade.mapper.GoodsMapper;
import com.campus.trade.mapper.OrderMapper;
import com.campus.trade.mapper.UserMapper;
import com.campus.trade.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 订单服务单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("订单服务 - 单元测试")
class OrderServiceTest {

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private GoodsMapper goodsMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order testOrder;
    private Goods testGoods;
    private User testBuyer;
    private User testSeller;

    @BeforeEach
    void setUp() {
        testGoods = new Goods();
        testGoods.setId(100L);
        testGoods.setTitle("二手iPhone");
        testGoods.setPrice(new BigDecimal("2000"));
        testGoods.setUserId(200L);
        testGoods.setStatus(1);

        testBuyer = new User();
        testBuyer.setId(300L);
        testBuyer.setNickname("买家小红");
        testBuyer.setCreditScore(70);

        testSeller = new User();
        testSeller.setId(200L);
        testSeller.setNickname("卖家小明");
        testSeller.setCreditScore(80);

        testOrder = new Order();
        testOrder.setId(1000L);
        testOrder.setOrderNo("ORD20260001");
        testOrder.setGoodsId(100L);
        testOrder.setBuyerId(300L);
        testOrder.setSellerId(200L);
        testOrder.setPrice(new BigDecimal("2000"));
        testOrder.setStatus(1);
        testOrder.setAddress("1号宿舍楼101");
    }

    // ==================== 创建订单测试 ====================

    @Test
    @DisplayName("【创建订单】正常情况：买家成功下单")
    void testCreateOrder_Success() {
        when(goodsMapper.findById(100L)).thenReturn(testGoods);
        when(orderMapper.insert(any(Order.class))).thenReturn(1);

        Order result = orderService.createOrder(300L, 100L, "1号宿舍楼101");

        assertNotNull(result);
        verify(orderMapper, times(1)).insert(any(Order.class));
    }

    @Test
    @DisplayName("【创建订单】异常情况：商品不存在")
    void testCreateOrder_GoodsNotFound() {
        when(goodsMapper.findById(999L)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> {
            orderService.createOrder(300L, 999L, "地址");
        });
    }

    @Test
    @DisplayName("【创建订单】异常情况：商品已下架")
    void testCreateOrder_GoodsOffShelf() {
        testGoods.setStatus(2);
        when(goodsMapper.findById(100L)).thenReturn(testGoods);

        assertThrows(RuntimeException.class, () -> {
            orderService.createOrder(300L, 100L, "地址");
        });
    }

    @Test
    @DisplayName("【创建订单】异常情况：商品已售出")
    void testCreateOrder_GoodsSold() {
        testGoods.setStatus(3);
        when(goodsMapper.findById(100L)).thenReturn(testGoods);

        assertThrows(RuntimeException.class, () -> {
            orderService.createOrder(300L, 100L, "地址");
        });
    }

    // ==================== 付款测试 ====================

    @Test
    @DisplayName("【付款】正常情况：买家付款成功")
    void testPayOrder_Success() {
        when(orderMapper.findById(1000L)).thenReturn(testOrder);
        when(orderMapper.update(any(Order.class))).thenReturn(1);

        orderService.payOrder(300L, 1000L);

        verify(orderMapper, times(1)).update(any(Order.class));
    }

    @Test
    @DisplayName("【付款】异常情况：订单不存在")
    void testPayOrder_OrderNotFound() {
        when(orderMapper.findById(999L)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> {
            orderService.payOrder(300L, 999L);
        });
    }

    @Test
    @DisplayName("【付款】异常情况：非买家付款")
    void testPayOrder_NotBuyer() {
        when(orderMapper.findById(1000L)).thenReturn(testOrder);

        assertThrows(RuntimeException.class, () -> {
            orderService.payOrder(999L, 1000L);
        });
    }

    // ==================== 发货测试 ====================

    @Test
    @DisplayName("【发货】卖家发货成功")
    void testShipOrder_Success() {
        testOrder.setStatus(2);
        when(orderMapper.findById(1000L)).thenReturn(testOrder);
        when(orderMapper.update(any(Order.class))).thenReturn(1);

        orderService.shipOrder(200L, 1000L);

        verify(orderMapper, times(1)).update(any(Order.class));
    }

    @Test
    @DisplayName("【发货】异常情况：非卖家发货")
    void testShipOrder_NotSeller() {
        testOrder.setStatus(2);
        when(orderMapper.findById(1000L)).thenReturn(testOrder);

        assertThrows(RuntimeException.class, () -> {
            orderService.shipOrder(999L, 1000L);
        });
    }

    // ==================== 确认收货测试 ====================

    @Test
    @DisplayName("【确认收货】买家确认收货成功")
    void testConfirmOrder_Success() {
        testOrder.setStatus(3);
        when(orderMapper.findById(1000L)).thenReturn(testOrder);
        when(orderMapper.update(any(Order.class))).thenReturn(1);

        orderService.confirmOrder(300L, 1000L);

        verify(orderMapper, times(1)).update(any(Order.class));
    }

    @Test
    @DisplayName("【确认收货】异常情况：非买家确认收货")
    void testConfirmOrder_NotBuyer() {
        testOrder.setStatus(3);
        when(orderMapper.findById(1000L)).thenReturn(testOrder);

        assertThrows(RuntimeException.class, () -> {
            orderService.confirmOrder(999L, 1000L);
        });
    }

    // ==================== 取消订单测试 ====================

    @Test
    @DisplayName("【取消订单】取消待付款订单")
    void testCancelOrder_PendingPayment() {
        testOrder.setStatus(1);

        when(orderMapper.findById(1000L)).thenReturn(testOrder);
        when(orderMapper.update(any(Order.class))).thenReturn(1);

        orderService.cancelOrder(300L, 1000L);

        verify(orderMapper, times(1)).update(any(Order.class));
    }

    @Test
    @DisplayName("【取消订单】已完成订单不能取消")
    void testCancelOrder_CompletedOrder() {
        testOrder.setStatus(4);
        when(orderMapper.findById(1000L)).thenReturn(testOrder);

        assertThrows(RuntimeException.class, () -> {
            orderService.cancelOrder(300L, 1000L);
        });
    }

    // ==================== 查询订单测试 ====================

    @Test
    @DisplayName("【查询订单】根据ID查询订单详情")
    void testGetOrderDetail_Success() {
        when(orderMapper.findById(1000L)).thenReturn(testOrder);

        Order result = orderService.getOrderDetail(1000L);

        assertNotNull(result);
        assertEquals(1000L, result.getId());
        verify(orderMapper, times(1)).findById(1000L);
    }

    @Test
    @DisplayName("【查询订单】订单不存在抛异常")
    void testGetOrderDetail_NotFound() {
        when(orderMapper.findById(999L)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> {
            orderService.getOrderDetail(999L);
        });
    }

    @Test
    @DisplayName("【查询订单】买家订单列表")
    void testGetBuyerOrders_Success() {
        List<Order> mockOrders = Arrays.asList(testOrder, new Order());
        when(orderMapper.findByBuyerId(300L)).thenReturn(mockOrders);

        List<Order> result = orderService.getBuyerOrders(300L);

        assertEquals(2, result.size());
        verify(orderMapper, times(1)).findByBuyerId(300L);
    }

    @Test
    @DisplayName("【查询订单】卖家订单列表")
    void testGetSellerOrders_Success() {
        List<Order> mockOrders = Arrays.asList(testOrder);
        when(orderMapper.findBySellerId(200L)).thenReturn(mockOrders);

        List<Order> result = orderService.getSellerOrders(200L);

        assertEquals(1, result.size());
        verify(orderMapper, times(1)).findBySellerId(200L);
    }

    @Test
    @DisplayName("【查询订单】买家无订单返回空列表")
    void testGetBuyerOrders_NoOrders() {
        when(orderMapper.findByBuyerId(999L)).thenReturn(Collections.emptyList());

        List<Order> result = orderService.getBuyerOrders(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("【查询订单】卖家无订单返回空列表")
    void testGetSellerOrders_NoOrders() {
        when(orderMapper.findBySellerId(999L)).thenReturn(Collections.emptyList());

        List<Order> result = orderService.getSellerOrders(999L);

        assertTrue(result.isEmpty());
    }
}