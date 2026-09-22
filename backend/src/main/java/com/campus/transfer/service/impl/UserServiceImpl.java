package com.campus.transfer.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.transfer.common.BusinessException;
import com.campus.transfer.config.AppProperties;
import com.campus.transfer.dto.ProfileRequest;
import com.campus.transfer.entity.User;
import com.campus.transfer.mapper.UserMapper;
import com.campus.transfer.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private AppProperties props;

    @Resource
    private com.campus.transfer.config.JwtUtil jwtUtil;

    /** 密码加密：MD5(明文 + 盐) */
    private String encrypt(String rawPassword) {
        return SecureUtil.md5(rawPassword + props.getSalt());
    }

    @Override
    public User register(String account, String password, String nickname, String phone) {
        if (StrUtil.isBlank(account) || StrUtil.isBlank(password)) {
            throw new BusinessException("账号和密码不能为空");
        }
        if (account.length() < 3 || account.length() > 20) {
            throw new BusinessException("账号长度需在 3-20 位之间");
        }
        if (password.length() < 6 || password.length() > 20) {
            throw new BusinessException("密码长度需在 6-20 位之间");
        }
        Long count = userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getAccount, account));
        if (count != null && count > 0) {
            throw new BusinessException("账号已存在，请更换");
        }
        User user = new User();
        user.setAccount(account);
        user.setPassword(encrypt(password));
        user.setNickname(StrUtil.isBlank(nickname) ? account : nickname);
        user.setAvatar("");
        user.setRole(0);
        user.setPhone(phone);
        user.setStatus(0);
        user.setCreateTime(new Date());
        userMapper.insert(user);
        return user;
    }

    @Override
    public Map<String, Object> login(String account, String password) {
        if (StrUtil.isBlank(account) || StrUtil.isBlank(password)) {
            throw new BusinessException("账号和密码不能为空");
        }
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getAccount, account));
        if (user == null || !user.getPassword().equals(encrypt(password))) {
            throw new BusinessException("账号或密码错误");
        }
        if (user.getStatus() != null && user.getStatus() == 1) {
            throw new BusinessException("账号已被禁用，请联系管理员");
        }
        String token = jwtUtil.createToken(user);
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", user);
        return result;
    }

    @Override
    public void updateProfile(ProfileRequest request) {
        Long userId = com.campus.transfer.common.UserContext.getUserId();
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(401, "账号不存在");
        }
        if (StrUtil.isNotBlank(request.getNickname())) {
            user.setNickname(request.getNickname());
        }
        if (StrUtil.isNotBlank(request.getPhone())) {
            user.setPhone(request.getPhone());
        }
        if (StrUtil.isNotBlank(request.getAvatar())) {
            user.setAvatar(request.getAvatar());
        }
        userMapper.updateById(user);
    }

    @Override
    public IPage<User> pageUsers(Integer page, Integer size, String keyword) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(User::getAccount, keyword).or().like(User::getNickname, keyword));
        }
        wrapper.orderByDesc(User::getCreateTime);
        return userMapper.selectPage(new Page<>(page, size), wrapper);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        if (status == null || (status != 0 && status != 1)) {
            throw new BusinessException("状态参数不合法");
        }
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (user.getRole() != null && user.getRole() == 1) {
            throw new BusinessException("不能禁用管理员账号");
        }
        user.setStatus(status);
        userMapper.updateById(user);
    }
}
