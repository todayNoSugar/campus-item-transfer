package com.campus.transfer.config;

import cn.hutool.core.util.StrUtil;
import com.campus.transfer.common.AdminOnly;
import com.campus.transfer.common.BusinessException;
import com.campus.transfer.common.NoAuth;
import com.campus.transfer.common.UserContext;
import com.campus.transfer.entity.User;
import com.campus.transfer.mapper.UserMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登录鉴权拦截器：
 * 1. 标注 @NoAuth 的接口放行；
 * 2. 其余接口必须携带有效 token；
 * 3. 标注 @AdminOnly 的接口校验管理员角色；
 * 4. 被管理员禁用的账号禁止操作。
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Resource
    private JwtUtil jwtUtil;

    @Resource
    private UserMapper userMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;

        // 无需登录的接口：仍支持"可选登录"——若携带有效 token 则解析用户上下文
        // （用于详情页判断收藏状态、发布者权限等），无效 token 按游客处理
        NoAuth noAuth = handlerMethod.getMethodAnnotation(NoAuth.class);
        if (noAuth == null) {
            noAuth = handlerMethod.getBeanType().getAnnotation(NoAuth.class);
        }
        if (noAuth != null) {
            String optionalToken = request.getHeader("token");
            if (StrUtil.isNotBlank(optionalToken) && jwtUtil.verify(optionalToken)) {
                User dbUser = userMapper.selectById(jwtUtil.getUserId(optionalToken));
                if (dbUser != null && dbUser.getStatus() != null && dbUser.getStatus() == 0) {
                    UserContext.set(dbUser);
                }
            }
            return true;
        }

        // 校验 token
        String token = request.getHeader("token");
        if (StrUtil.isBlank(token) || !jwtUtil.verify(token)) {
            throw new BusinessException(401, "未登录或登录已过期");
        }

        // 从数据库加载用户，校验账号是否被禁用
        User dbUser = userMapper.selectById(jwtUtil.getUserId(token));
        if (dbUser == null) {
            throw new BusinessException(401, "账号不存在");
        }
        if (dbUser.getStatus() != null && dbUser.getStatus() == 1) {
            throw new BusinessException(403, "账号已被禁用，请联系管理员");
        }
        UserContext.set(dbUser);

        // 管理员接口校验
        AdminOnly adminOnly = handlerMethod.getMethodAnnotation(AdminOnly.class);
        if (adminOnly == null) {
            adminOnly = handlerMethod.getBeanType().getAnnotation(AdminOnly.class);
        }
        if (adminOnly != null && (dbUser.getRole() == null || dbUser.getRole() != 1)) {
            throw new BusinessException(403, "无管理员权限");
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }
}
