package com.campus.transfer.dto;

import lombok.Data;

import java.util.Date;

/**
 * 留言视图对象（含留言人信息）
 */
@Data
public class MessageVO {
    private Long id;
    private Long goodsId;
    private Long userId;
    private String content;
    private String reply;
    private Date createTime;
    /** 留言人昵称 */
    private String nickname;
    /** 留言人头像 */
    private String avatar;
}
