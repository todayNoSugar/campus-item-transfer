package com.campus.transfer.controller;

import com.campus.transfer.common.Result;
import com.campus.transfer.dto.ReserveRequest;
import com.campus.transfer.dto.ReserveVO;
import com.campus.transfer.dto.StatusRequest;
import com.campus.transfer.service.ReserveService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 预约接口
 */
@RestController
@RequestMapping("/api")
public class ReserveController {

    @Resource
    private ReserveService reserveService;

    /** 对某物品发起预约 */
    @PostMapping("/goods/{goodsId}/reserve")
    public Result<Void> create(@PathVariable Long goodsId, @RequestBody ReserveRequest request) {
        reserveService.create(goodsId, request);
        return Result.ok();
    }

    /** 我发起的预约 */
    @GetMapping("/reserve/my")
    public Result<List<ReserveVO>> my() {
        return Result.ok(reserveService.listMy());
    }

    /** 别人预约我的物品 */
    @GetMapping("/reserve/to-me")
    public Result<List<ReserveVO>> toMe() {
        return Result.ok(reserveService.listToMe());
    }

    /** 同意/拒绝预约：1同意 2拒绝 */
    @PutMapping("/reserve/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody StatusRequest request) {
        reserveService.updateStatus(id, request.getStatus());
        return Result.ok();
    }
}
