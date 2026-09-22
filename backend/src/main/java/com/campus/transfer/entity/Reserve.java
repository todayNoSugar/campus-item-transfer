package com.campus.transfer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 预约表
 */
@Data
@TableName("reserve")
public class Reserve {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 物品id */
    private Long goodsId;

    /** 预约人id */
    private Long userId;

    /** 物品发布者id */
    private Long ownerId;

    /** 期望预约时间 */
    private Date reserveTime;

    /** 状态: 0待确认 1同意 2拒绝 */
    private Integer status;

    /** 预约备注 */
    private String remark;

    /** 发起时间 */
    private Date createTime;
}
