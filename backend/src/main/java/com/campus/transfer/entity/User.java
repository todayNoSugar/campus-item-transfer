package com.campus.transfer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.Date;

/**
 * 用户表
 */
@Data
@TableName("user")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 登录账号 */
    private String account;

    /** 密码(MD5加盐)，返回前端时忽略 */
    @JsonIgnore
    private String password;

    /** 昵称 */
    private String nickname;

    /** 头像地址 */
    private String avatar;

    /** 角色: 0普通用户 1管理员 */
    private Integer role;

    /** 手机号 */
    private String phone;

    /** 状态: 0正常 1禁用 */
    private Integer status;

    /** 注册时间 */
    private Date createTime;
}
