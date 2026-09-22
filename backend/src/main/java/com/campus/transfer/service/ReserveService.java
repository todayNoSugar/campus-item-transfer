package com.campus.transfer.service;

import com.campus.transfer.dto.ReserveRequest;
import com.campus.transfer.dto.ReserveVO;

import java.util.List;

public interface ReserveService {

    /** 发起预约 */
    void create(Long goodsId, ReserveRequest request);

    /** 我发起的预约 */
    List<ReserveVO> listMy();

    /** 别人预约我的物品 */
    List<ReserveVO> listToMe();

    /** 发布者同意/拒绝预约：1同意 2拒绝 */
    void updateStatus(Long id, Integer status);
}
