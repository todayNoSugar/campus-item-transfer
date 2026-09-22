package com.campus.transfer.controller;

import com.campus.transfer.common.NoAuth;
import com.campus.transfer.common.Result;
import com.campus.transfer.common.UserContext;
import com.campus.transfer.dto.LoginRequest;
import com.campus.transfer.dto.RegisterRequest;
import com.campus.transfer.entity.User;
import com.campus.transfer.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 认证接口：注册 / 登录 / 当前用户信息 / 退出
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Resource
    private UserService userService;

    @NoAuth
    @PostMapping("/register")
    public Result<User> register(@RequestBody RegisterRequest request) {
        return Result.ok(userService.register(request.getAccount(), request.getPassword(),
                request.getNickname(), request.getPhone()));
    }

    @NoAuth
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginRequest request) {
        return Result.ok(userService.login(request.getAccount(), request.getPassword()));
    }

    /** 当前登录用户信息 */
    @GetMapping("/info")
    public Result<User> info() {
        return Result.ok(UserContext.get());
    }

    /** 退出登录（前端清除 token 即可） */
    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.ok();
    }
}
