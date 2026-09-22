package com.campus.transfer.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 应用自定义配置（对应 application.yml 中的 app 节点）
 */
@Data
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private Jwt jwt = new Jwt();

    /** 密码加盐值 */
    private String salt;

    private Upload upload = new Upload();

    @Data
    public static class Jwt {
        /** 签名密钥 */
        private String secret;
        /** token 有效期（小时） */
        private Long expireHours;
    }

    @Data
    public static class Upload {
        /** 图片保存目录（相对运行目录） */
        private String dir;
    }
}
