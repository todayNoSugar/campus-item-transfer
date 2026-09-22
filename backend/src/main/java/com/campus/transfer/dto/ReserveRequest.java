package com.campus.transfer.dto;

import lombok.Data;

/**
 * 预约请求
 */
@Data
public class ReserveRequest {
    /** 期望预约时间（yyyy-MM-dd HH:mm:ss） */
    private String reserveTime;
    /** 备注 */
    private String remark;
}
