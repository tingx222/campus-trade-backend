package com.campus.trade.controller;

import com.campus.trade.common.ResultVO;
import com.campus.trade.entity.Order;
import com.campus.trade.service.OrderService;
import com.campus.trade.util.JwtUtil;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    private final OrderService orderService;
    private final JwtUtil jwtUtil;

    public OrderController(OrderService orderService, JwtUtil jwtUtil) {
        this.orderService = orderService;
        this.jwtUtil = jwtUtil;
    }

    /** 下单 POST /api/order/create */
    @PostMapping("/create")
    public ResultVO<Order> create(@RequestHeader("Authorization") String token,
                                  @RequestBody Map<String, Object> params) {
        try {
            Long userId = getUserId(token);
            Long goodsId = Long.valueOf(params.get("goodsId").toString());
            String address = params.get("address").toString();
            Order order = orderService.createOrder(userId, goodsId, address);
            return ResultVO.success("下单成功", order);
        } catch (RuntimeException e) {
            return ResultVO.fail(e.getMessage());
        }
    }

    /** 付款 POST /api/order/pay */
    @PostMapping("/pay")
    public ResultVO<Void> pay(@RequestHeader("Authorization") String token,
                              @RequestParam Long orderId) {
        try {
            Long userId = getUserId(token);
            orderService.payOrder(userId, orderId);
            return ResultVO.success("付款成功", null);
        } catch (RuntimeException e) {
            return ResultVO.fail(e.getMessage());
        }
    }

    /** 发货 POST /api/order/ship */
    @PostMapping("/ship")
    public ResultVO<Void> ship(@RequestHeader("Authorization") String token,
                               @RequestParam Long orderId) {
        try {
            Long userId = getUserId(token);
            orderService.shipOrder(userId, orderId);
            return ResultVO.success("发货成功", null);
        } catch (RuntimeException e) {
            return ResultVO.fail(e.getMessage());
        }
    }

    /** 确认收货 POST /api/order/confirm */
    @PostMapping("/confirm")
    public ResultVO<Void> confirm(@RequestHeader("Authorization") String token,
                                  @RequestParam Long orderId) {
        try {
            Long userId = getUserId(token);
            orderService.confirmOrder(userId, orderId);
            return ResultVO.success("确认收货成功", null);
        } catch (RuntimeException e) {
            return ResultVO.fail(e.getMessage());
        }
    }

    /** 取消订单 POST /api/order/cancel */
    @PostMapping("/cancel")
    public ResultVO<Void> cancel(@RequestHeader("Authorization") String token,
                                 @RequestParam Long orderId) {
        try {
            Long userId = getUserId(token);
            orderService.cancelOrder(userId, orderId);
            return ResultVO.success("订单已取消", null);
        } catch (RuntimeException e) {
            return ResultVO.fail(e.getMessage());
        }
    }

    /** 订单详情 GET /api/order/detail */
    @GetMapping("/detail")
    public ResultVO<Order> detail(@RequestParam Long id) {
        try {
            Order order = orderService.getOrderDetail(id);
            return ResultVO.success(order);
        } catch (RuntimeException e) {
            return ResultVO.fail(e.getMessage());
        }
    }

    /** 买家订单列表 GET /api/order/buyer */
    @GetMapping("/buyer")
    public ResultVO<List<Order>> buyerOrders(@RequestHeader("Authorization") String token) {
        try {
            Long userId = getUserId(token);
            List<Order> list = orderService.getBuyerOrders(userId);
            return ResultVO.success(list);
        } catch (RuntimeException e) {
            return ResultVO.fail(e.getMessage());
        }
    }

    /** 卖家订单列表 GET /api/order/seller */
    @GetMapping("/seller")
    public ResultVO<List<Order>> sellerOrders(@RequestHeader("Authorization") String token) {
        try {
            Long userId = getUserId(token);
            List<Order> list = orderService.getSellerOrders(userId);
            return ResultVO.success(list);
        } catch (RuntimeException e) {
            return ResultVO.fail(e.getMessage());
        }
    }

    private Long getUserId(String token) {
        String tokenValue = token.startsWith("Bearer ") ? token.substring(7) : token;
        return jwtUtil.getUserId(tokenValue);
    }
}