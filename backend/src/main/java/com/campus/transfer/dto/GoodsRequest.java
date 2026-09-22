package com.campus.transfer.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 发布 / 修改物品请求
 */
@Data
public class GoodsRequest {
    private String title;
    private String description;
    private Long categoryId;
    /** 图片地址，多张逗号分隔 */
    private String images;
    /** 期望转让价格 */
    private BigDecimal price;
    /** 流转类型: 0标价转让 1以物换物 */
    private Integer type;
    /** 交易地点 */
    private String location;
}
