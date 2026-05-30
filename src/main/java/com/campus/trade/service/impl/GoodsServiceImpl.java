package com.campus.trade.service.impl;

import com.campus.trade.entity.Goods;
import com.campus.trade.mapper.GoodsMapper;
import com.campus.trade.service.GoodsService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品服务实现类
 * 单一职责：仅处理商品相关业务逻辑
 */
@Service
public class GoodsServiceImpl implements GoodsService {

    private final GoodsMapper goodsMapper;

    public GoodsServiceImpl(GoodsMapper goodsMapper) {
        this.goodsMapper = goodsMapper;
    }

    @Override
    public Goods publishGoods(Long userId, Goods goods) {
        // 参数校验
        validateGoods(goods);

        // 防止重复发布：同一卖家不能发布完全重复标题的商品
        int duplicateCount = goodsMapper.countByUserIdAndTitle(userId, goods.getTitle());
        if (duplicateCount > 0) {
            throw new RuntimeException("您已发布过相同标题的商品，请勿重复发布");
        }

        goods.setUserId(userId);
        goods.setStatus(1); // 默认上架
        goods.setCreateTime(LocalDateTime.now());

        goodsMapper.insert(goods);
        return goods;
    }

    @Override
    public void editGoods(Long userId, Goods goods) {
        // 校验商品归属
        Goods existing = goodsMapper.findById(goods.getId());
        if (existing == null) {
            throw new RuntimeException("商品不存在");
        }
        if (!existing.getUserId().equals(userId)) {
            throw new RuntimeException("无权编辑他人商品");
        }
        if (existing.getStatus() == 3) {
            throw new RuntimeException("已售出商品不能编辑");
        }

        // 更新允许修改的字段
        Goods update = new Goods();
        update.setId(goods.getId());
        update.setTitle(goods.getTitle());
        update.setDescription(goods.getDescription());
        update.setPrice(goods.getPrice());
        update.setCategory(goods.getCategory());
        update.setPics(goods.getPics());

        goodsMapper.update(update);
    }

    @Override
    public void offShelfGoods(Long userId, Long goodsId) {
        Goods existing = goodsMapper.findById(goodsId);
        if (existing == null) {
            throw new RuntimeException("商品不存在");
        }
        if (!existing.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作他人商品");
        }

        Goods update = new Goods();
        update.setId(goodsId);
        update.setStatus(2); // 下架
        goodsMapper.update(update);
    }

    @Override
    public Goods getGoodsDetail(Long goodsId) {
        Goods goods = goodsMapper.findById(goodsId);
        if (goods == null) {
            throw new RuntimeException("商品不存在");
        }
        return goods;
    }

    @Override
    public Map<String, Object> searchGoods(String keyword, String category,
                                              BigDecimal minPrice, BigDecimal maxPrice,
                                              String orderBy, int page, int size) {
        // 参数安全处理
        int safePage = Math.max(1, page);
        int safeSize = Math.max(1, Math.min(size, 50));
        int offset = (safePage - 1) * safeSize;

        // 默认排序
        if (orderBy == null || orderBy.isEmpty()) {
            orderBy = "time";
        }

        List<Goods> list = goodsMapper.search(keyword, category, minPrice, maxPrice, orderBy, offset, safeSize);
        int total = goodsMapper.countSearch(keyword, category, minPrice, maxPrice);

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("page", safePage);
        result.put("size", safeSize);
        result.put("totalPages", (total + safeSize - 1) / safeSize);
        return result;
    }

    @Override
    public List<Goods> getMyGoods(Long userId) {
        return goodsMapper.findByUserId(userId);
    }

    /**
     * 商品参数校验
     */
    private void validateGoods(Goods goods) {
        if (goods.getTitle() == null || goods.getTitle().length() < 2 || goods.getTitle().length() > 50) {
            throw new RuntimeException("商品标题长度须在2-50个字符之间");
        }
        if (goods.getDescription() == null || goods.getDescription().length() < 10 || goods.getDescription().length() > 500) {
            throw new RuntimeException("商品描述长度须在10-500个字符之间");
        }
        if (goods.getPrice() == null || goods.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("价格不能为负数");
        }
        if (goods.getCategory() == null || goods.getCategory().isEmpty()) {
            throw new RuntimeException("请选择商品类别");
        }
    }
}
