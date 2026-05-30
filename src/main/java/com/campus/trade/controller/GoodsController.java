package com.campus.trade.controller;

import com.campus.trade.common.ResultVO;
import com.campus.trade.entity.Goods;
import com.campus.trade.service.GoodsService;
import com.campus.trade.util.JwtUtil;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 商品控制器
 * 单一职责：处理商品相关的HTTP请求
 */
@RestController
@RequestMapping("/api/goods")
public class GoodsController {

    private final GoodsService goodsService;
    private final JwtUtil jwtUtil;

    public GoodsController(GoodsService goodsService, JwtUtil jwtUtil) {
        this.goodsService = goodsService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 发布商品 POST /api/goods/publish
     */
    @PostMapping("/publish")
    public ResultVO<Goods> publish(@RequestHeader("Authorization") String token,
                                   @RequestBody Goods goods) {
        try {
            Long userId = getUserIdFromToken(token);
            Goods published = goodsService.publishGoods(userId, goods);
            return ResultVO.success("发布成功", published);
        } catch (RuntimeException e) {
            return ResultVO.fail(e.getMessage());
        }
    }

    /**
     * 编辑商品 PUT /api/goods/update
     */
    @PutMapping("/update")
    public ResultVO<Void> update(@RequestHeader("Authorization") String token,
                                 @RequestBody Goods goods) {
        try {
            Long userId = getUserIdFromToken(token);
            goodsService.editGoods(userId, goods);
            return ResultVO.success("编辑成功", null);
        } catch (RuntimeException e) {
            return ResultVO.fail(e.getMessage());
        }
    }

    /**
     * 商品下架 DELETE /api/goods/offShelf
     */
    @DeleteMapping("/offShelf")
    public ResultVO<Void> offShelf(@RequestHeader("Authorization") String token,
                                   @RequestParam Long goodsId) {
        try {
            Long userId = getUserIdFromToken(token);
            goodsService.offShelfGoods(userId, goodsId);
            return ResultVO.success("下架成功", null);
        } catch (RuntimeException e) {
            return ResultVO.fail(e.getMessage());
        }
    }

    /**
     * 商品搜索/列表 GET /api/goods/list
     */
    @GetMapping("/list")
    public ResultVO<Map<String, Object>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String orderBy,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        Map<String, Object> result = goodsService.searchGoods(keyword, category, minPrice, maxPrice, orderBy, page, size);
        return ResultVO.success(result);
    }

    /**
     * 商品详情 GET /api/goods/detail
     */
    @GetMapping("/detail")
    public ResultVO<Goods> detail(@RequestParam Long id) {
        try {
            Goods goods = goodsService.getGoodsDetail(id);
            return ResultVO.success(goods);
        } catch (RuntimeException e) {
            return ResultVO.fail(e.getMessage());
        }
    }

    /**
     * 我的商品列表 GET /api/goods/my
     */
    @GetMapping("/my")
    public ResultVO<List<Goods>> myGoods(@RequestHeader("Authorization") String token) {
        try {
            Long userId = getUserIdFromToken(token);
            List<Goods> list = goodsService.getMyGoods(userId);
            return ResultVO.success(list);
        } catch (RuntimeException e) {
            return ResultVO.fail(e.getMessage());
        }
    }

    /**
     * 从Token中提取用户ID
     */
    private Long getUserIdFromToken(String token) {
        String tokenValue = token.startsWith("Bearer ") ? token.substring(7) : token;
        return jwtUtil.getUserId(tokenValue);
    }
}
