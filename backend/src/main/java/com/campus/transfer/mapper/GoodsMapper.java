package com.campus.transfer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.transfer.entity.Goods;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface GoodsMapper extends BaseMapper<Goods> {

    /** 物品发布总量 */
    @Select("SELECT COUNT(*) FROM goods")
    Long countTotal();

    /** 成交数量 */
    @Select("SELECT COUNT(*) FROM goods WHERE status = 2")
    Long countDealt();

    /** 上架数量 */
    @Select("SELECT COUNT(*) FROM goods WHERE status = 1")
    Long countOnline();

    /** 待审核数量 */
    @Select("SELECT COUNT(*) FROM goods WHERE status = 0")
    Long countPending();

    /** 各分类物品数量（分类占比） */
    @Select("SELECT c.id AS categoryId, c.name AS categoryName, COUNT(g.id) AS cnt " +
            "FROM category c LEFT JOIN goods g ON g.category_id = c.id " +
            "GROUP BY c.id, c.name ORDER BY c.sort")
    List<Map<String, Object>> countGroupByCategory();

    /** 各状态物品数量（状态分布） */
    @Select("SELECT status, COUNT(*) AS cnt FROM goods GROUP BY status")
    List<Map<String, Object>> countGroupByStatus();

    /** 近7天每日发布量 */
    @Select("SELECT DATE(create_time) AS day, COUNT(*) AS cnt FROM goods " +
            "WHERE create_time >= DATE_SUB(CURDATE(), INTERVAL 6 DAY) " +
            "GROUP BY DATE(create_time) ORDER BY day")
    List<Map<String, Object>> countLast7Days();
}
