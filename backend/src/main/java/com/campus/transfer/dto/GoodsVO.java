package com.campus.transfer.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 物品视图对象（含发布者信息、分类名、留言数、收藏状态）
 */
@Data
public class GoodsVO {
    private Long id;
    private Long userId;
    private String title;
    private String description;
    private Long categoryId;
    private String categoryName;
    /** 图片地址，多张逗号分隔 */
    private String images;
    private BigDecimal price;
    /** 流转类型: 0标价转让 1以物换物 */
    private Integer type;
    private String location;
    /** 状态: 0待审核 1上架 2已成交 3下架 */
    private Integer status;
    private Integer viewCount;
    private Date createTime;
    /** 发布人昵称 */
    private String ownerNickname;
    /** 发布人头像 */
    private String ownerAvatar;
    /** 留言数 */
    private Long messageCount;
    /** 当前用户是否已收藏 */
    private Boolean favorite = false;
}
