package com.campus.transfer.config;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTValidator;
import cn.hutool.jwt.signers.JWTSignerUtil;
import com.campus.transfer.entity.User;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 工具类（基于 Hutool）
 */
@Component
public class JwtUtil {

    @Resource
    private AppProperties props;

    /** 生成 token */
    public String createToken(User user) {
        Date now = new Date();
        Date expire = DateUtil.offsetHour(now, props.getJwt().getExpireHours().intValue());
        return JWT.create()
                .setPayload("userId", user.getId())
                .setPayload("account", user.getAccount())
                .setPayload("role", user.getRole())
                .setIssuedAt(now)
                .setExpiresAt(expire)
                .setKey(key())
                .sign();
    }

    /** 校验 token 签名与有效期 */
    public boolean verify(String token) {
        try {
            JWTValidator.of(token)
                    .validateAlgorithm(JWTSignerUtil.hs256(key()))
                    .validateDate();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Long getUserId(String token) {
        return Convert.toLong(getJwt(token).getPayload("userId"));
    }

    public String getAccount(String token) {
        return Convert.toStr(getJwt(token).getPayload("account"));
    }

    public Integer getRole(String token) {
        return Convert.toInt(getJwt(token).getPayload("role"), 0);
    }

    private JWT getJwt(String token) {
        return JWT.of(token).setKey(key());
    }

    private byte[] key() {
        return props.getJwt().getSecret().getBytes(StandardCharsets.UTF_8);
    }
}
