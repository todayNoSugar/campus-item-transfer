package com.campus.transfer.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 管理端数据看板统计
 */
@Data
public class StatsVO {
    /** 总发布物品数量 */
    private Long totalGoods;
    /** 成交数量 */
    private Long totalDealt;
    /** 上架数量 */
    private Long totalOnline;
    /** 待审核数量 */
    private Long totalPending;
    /** 平台用户总数 */
    private Long totalUsers;

    /** 各分类物品数量 [{categoryId, categoryName, cnt}] */
    private List<Map<String, Object>> categoryList;
    /** 各状态物品数量 [{status, cnt}] */
    private List<Map<String, Object>> statusList;
    /** 近7天每日发布量 [{day, cnt}] */
    private List<Map<String, Object>> last7Days;
}
