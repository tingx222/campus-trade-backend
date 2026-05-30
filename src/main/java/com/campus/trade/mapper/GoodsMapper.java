package com.campus.trade.mapper;

import com.campus.trade.entity.Goods;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品数据访问接口
 */
@Mapper
public interface GoodsMapper {

    /**
     * 新增商品
     */
    int insert(Goods goods);

    /**
     * 更新商品信息
     */
    int update(Goods goods);

    /**
     * 根据ID查询商品
     */
    Goods findById(@Param("id") Long id);

    /**
     * 查询指定用户的商品列表
     */
    List<Goods> findByUserId(@Param("userId") Long userId);

    /**
     * 搜索商品（关键词 + 分类 + 价格区间 + 排序 + 分页）
     */
    List<Goods> search(@Param("keyword") String keyword,
                        @Param("category") String category,
                        @Param("minPrice") java.math.BigDecimal minPrice,
                        @Param("maxPrice") java.math.BigDecimal maxPrice,
                        @Param("orderBy") String orderBy,
                        @Param("offset") int offset,
                        @Param("limit") int limit);

    /**
     * 统计搜索结果总数
     */
    int countSearch(@Param("keyword") String keyword,
                     @Param("category") String category,
                     @Param("minPrice") java.math.BigDecimal minPrice,
                     @Param("maxPrice") java.math.BigDecimal maxPrice);

    /**
     * 根据用户ID和标题检查是否重复
     */
    int countByUserIdAndTitle(@Param("userId") Long userId, @Param("title") String title);
}
