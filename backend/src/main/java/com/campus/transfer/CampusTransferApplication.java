package com.campus.transfer;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 校园闲置物品流转平台 - 启动类
 */
@SpringBootApplication
@MapperScan("com.campus.transfer.mapper")
public class CampusTransferApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampusTransferApplication.class, args);
        System.out.println("======== 校园闲置物品流转平台后端启动成功，端口 8080 ========");
    }
}
