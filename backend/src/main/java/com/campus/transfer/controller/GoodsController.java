package com.campus.transfer.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.campus.transfer.common.NoAuth;
import com.campus.transfer.common.Result;
import com.campus.transfer.dto.GoodsQuery;
import com.campus.transfer.dto.GoodsRequest;
import com.campus.transfer.dto.GoodsVO;
import com.campus.transfer.dto.StatusRequest;
import com.campus.transfer.service.GoodsService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 物品接口
 */
@RestController
@RequestMapping("/api/goods")
public class GoodsController {

    @Resource
    private GoodsService goodsService;

    /** 前台物品列表（仅上架，游客可访问） */
    @NoAuth
    @GetMapping
    public Result<IPage<GoodsVO>> page(GoodsQuery query) {
        return Result.ok(goodsService.page(query, false));
    }

    /** 物品详情（游客可访问） */
    @NoAuth
    @GetMapping("/{id}")
    public Result<GoodsVO> detail(@PathVariable Long id) {
        return Result.ok(goodsService.getDetail(id));
    }

    /** 发布物品 */
    @PostMapping
    public Result<Void> create(@RequestBody GoodsRequest request) {
        goodsService.create(request);
        return Result.ok();
    }

    /** 我发布的物品 */
    @GetMapping("/my")
    public Result<IPage<GoodsVO>> my(@RequestParam(defaultValue = "1") Integer page,
                                     @RequestParam(defaultValue = "10") Integer size) {
        return Result.ok(goodsService.listMy(page, size));
    }

    /** 修改物品 */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody GoodsRequest request) {
        goodsService.update(id, request);
        return Result.ok();
    }

    /** 变更物品状态：1上架 2已成交 3下架 */
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody StatusRequest request) {
        goodsService.updateStatus(id, request.getStatus());
        return Result.ok();
    }

    /** 删除物品 */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        goodsService.delete(id);
        return Result.ok();
    }
}
