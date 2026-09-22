package com.campus.transfer.dto;

import lombok.Data;

/**
 * 注册请求
 */
@Data
public class RegisterRequest {
    private String account;
    private String password;
    private String nickname;
    private String phone;
}
