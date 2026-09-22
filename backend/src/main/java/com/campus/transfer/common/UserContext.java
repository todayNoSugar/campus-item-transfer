package com.campus.transfer.common;

import com.campus.transfer.entity.User;

/**
 * 当前登录用户上下文（ThreadLocal）
 */
public class UserContext {

    private static final ThreadLocal<User> HOLDER = new ThreadLocal<>();

    public static void set(User user) {
        HOLDER.set(user);
    }

    public static User get() {
        return HOLDER.get();
    }

    /** 当前登录用户id */
    public static Long getUserId() {
        User user = HOLDER.get();
        return user == null ? null : user.getId();
    }

    /** 当前用户是否为管理员 */
    public static boolean isAdmin() {
        User user = HOLDER.get();
        return user != null && user.getRole() != null && user.getRole() == 1;
    }

    public static void clear() {
        HOLDER.remove();
    }
}
