package com.campus.transfer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 闲置物品表
 */
@Data
@TableName("goods")
public class Goods {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 发布人id */
    private Long userId;

    /** 标题 */
    private String title;

    /** 描述 */
    private String description;

    /** 分类id */
    private Long categoryId;

    /** 图片地址，多张逗号分隔 */
    private String images;

    /** 期望转让价格(标价转让时填写) */
    private BigDecimal price;

    /** 流转类型: 0标价转让 1以物换物 */
    private Integer type;

    /** 期望交易地点(校园地点) */
    private String location;

    /** 状态: 0待审核 1上架 2已成交 3下架 */
    private Integer status;

    /** 浏览次数 */
    private Integer viewCount;

    /** 发布时间 */
    private Date createTime;
}
