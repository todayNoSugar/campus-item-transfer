package com.campus.transfer.dto;

import lombok.Data;

import java.util.Date;

/**
 * 预约视图对象（含物品与双方信息）
 */
@Data
public class ReserveVO {
    private Long id;
    private Long goodsId;
    private Long userId;
    private Long ownerId;
    /** 期望预约时间 */
    private Date reserveTime;
    /** 状态: 0待确认 1同意 2拒绝 */
    private Integer status;
    private String remark;
    private Date createTime;

    /** 物品标题 */
    private String goodsTitle;
    /** 物品首图 */
    private String goodsImage;
    /** 物品状态 */
    private Integer goodsStatus;

    /** 预约人昵称/头像 */
    private String userName;
    private String userAvatar;
    /** 发布者昵称/头像 */
    private String ownerName;
    private String ownerAvatar;
}
