package com.campus.transfer.dto;

import lombok.Data;

/**
 * 登录请求
 */
@Data
public class LoginRequest {
    private String account;
    private String password;
}
