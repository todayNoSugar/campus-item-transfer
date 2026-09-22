package com.campus.transfer.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.campus.transfer.dto.ProfileRequest;
import com.campus.transfer.entity.User;

import java.util.Map;

public interface UserService {

    /** 注册 */
    User register(String account, String password, String nickname, String phone);

    /** 登录，返回 {token, user} */
    Map<String, Object> login(String account, String password);

    /** 修改个人资料 */
    void updateProfile(ProfileRequest request);

    /** 管理员：分页查询用户 */
    IPage<User> pageUsers(Integer page, Integer size, String keyword);

    /** 管理员：禁用/启用用户 */
    void updateStatus(Long id, Integer status);
}
