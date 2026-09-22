package com.campus.transfer.dto;

import lombok.Data;

/**
 * 物品列表查询参数
 */
@Data
public class GoodsQuery {
    private Integer page = 1;
    private Integer size = 12;
    /** 关键词（标题模糊匹配） */
    private String keyword;
    /** 分类id */
    private Long categoryId;
    /** 流转类型 */
    private Integer type;
    /** 状态筛选（管理员用） */
    private Integer status;
}
