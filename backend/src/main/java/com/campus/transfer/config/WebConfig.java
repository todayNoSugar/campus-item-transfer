package com.campus.transfer.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import java.io.File;

/**
 * Web 配置：跨域、拦截器、上传目录静态映射
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Resource
    private AuthInterceptor authInterceptor;

    @Resource
    private AppProperties props;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor).addPathPatterns("/api/**");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 上传目录映射为 /upload/** 静态资源，浏览器可直接访问图片
        // 使用 toURI 转换为标准 file 格式，兼容 Windows / Linux 路径
        File dir = new File(System.getProperty("user.dir"), props.getUpload().getDir());
        String location = dir.toURI().toString();
        if (!location.endsWith("/")) {
            location += "/";
        }
        registry.addResourceHandler("/upload/**")
                .addResourceLocations(location);
    }
}
