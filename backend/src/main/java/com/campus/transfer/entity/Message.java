package com.campus.transfer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 留言表
 */
@Data
@TableName("message")
public class Message {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 物品id */
    private Long goodsId;

    /** 留言人id */
    private Long userId;

    /** 留言内容 */
    private String content;

    /** 发布者回复内容 */
    private String reply;

    /** 留言时间 */
    private Date createTime;
}
