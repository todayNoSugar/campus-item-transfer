package com.campus.transfer.dto;

import lombok.Data;

/**
 * 修改个人资料请求
 */
@Data
public class ProfileRequest {
    private String nickname;
    private String phone;
    private String avatar;
}
