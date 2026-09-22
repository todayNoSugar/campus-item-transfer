package com.campus.transfer.dto;

import lombok.Data;

/**
 * 状态变更请求（通用：物品状态、预约状态、用户状态）
 */
@Data
public class StatusRequest {
    private Integer status;
}
