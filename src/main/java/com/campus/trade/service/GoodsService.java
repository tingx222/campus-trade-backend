package com.campus.trade.service;

import com.campus.trade.entity.Goods;

import java.util.List;
import java.util.Map;

/**
 * 商品服务接口（面向接口编程 - 依赖倒置原则）
 */
public interface GoodsService {

    /**
     * 发布商品
     */
    Goods publishGoods(Long userId, Goods goods);

    /**
     * 编辑商品信息
     */
    void editGoods(Long userId, Goods goods);

    /**
     * 商品下架
     */
    void offShelfGoods(Long userId, Long goodsId);

    /**
     * 根据ID查询商品详情
     */
    Goods getGoodsDetail(Long goodsId);

    /**
     * 搜索商品（关键词 + 分类 + 价格区间 + 排序 + 分页）
     */
    Map<String, Object> searchGoods(String keyword, String category,
                                     java.math.BigDecimal minPrice, java.math.BigDecimal maxPrice,
                                     String orderBy, int page, int size);

    /**
     * 查询我的商品列表
     */
    List<Goods> getMyGoods(Long userId);
}
