package com.campus.transfer.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.campus.transfer.common.AdminOnly;
import com.campus.transfer.common.Result;
import com.campus.transfer.dto.GoodsQuery;
import com.campus.transfer.dto.GoodsVO;
import com.campus.transfer.dto.MessageVO;
import com.campus.transfer.dto.StatsVO;
import com.campus.transfer.dto.StatusRequest;
import com.campus.transfer.entity.User;
import com.campus.transfer.service.GoodsService;
import com.campus.transfer.service.MessageService;
import com.campus.transfer.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 管理员后台接口（全部需要管理员权限）
 */
@AdminOnly
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Resource
    private UserService userService;
    @Resource
    private GoodsService goodsService;
    @Resource
    private MessageService messageService;

    // ---------- 用户管理 ----------

    /** 分页查询全部用户 */
    @GetMapping("/user/list")
    public Result<IPage<User>> users(@RequestParam(defaultValue = "1") Integer page,
                                     @RequestParam(defaultValue = "10") Integer size,
                                     @RequestParam(required = false) String keyword) {
        return Result.ok(userService.pageUsers(page, size, keyword));
    }

    /** 禁用/启用用户：0正常 1禁用 */
    @PutMapping("/user/{id}/status")
    public Result<Void> userStatus(@PathVariable Long id, @RequestBody StatusRequest request) {
        userService.updateStatus(id, request.getStatus());
        return Result.ok();
    }

    // ---------- 物品审核 ----------

    /** 分页查询全部物品（可按状态筛选） */
    @GetMapping("/goods/list")
    public Result<IPage<GoodsVO>> goods(GoodsQuery query) {
        return Result.ok(goodsService.page(query, true));
    }

    /** 审核物品：1通过上架 / 3拒绝下架 */
    @PutMapping("/goods/{id}/audit")
    public Result<Void> audit(@PathVariable Long id, @RequestBody StatusRequest request) {
        goodsService.audit(id, request.getStatus());
        return Result.ok();
    }

    /** 直接下架违规物品 */
    @PutMapping("/goods/{id}/offline")
    public Result<Void> offline(@PathVariable Long id) {
        goodsService.audit(id, 3);
        return Result.ok();
    }

    // ---------- 留言管理 ----------

    /** 分页查看全部留言 */
    @GetMapping("/message/list")
    public Result<IPage<MessageVO>> messages(@RequestParam(defaultValue = "1") Integer page,
                                             @RequestParam(defaultValue = "10") Integer size,
                                             @RequestParam(required = false) String keyword) {
        return Result.ok(messageService.pageAll(page, size, keyword));
    }

    /** 删除违规留言 */
    @DeleteMapping("/message/{id}")
    public Result<Void> deleteMessage(@PathVariable Long id) {
        messageService.delete(id);
        return Result.ok();
    }

    // ---------- 数据看板 ----------

    /** 平台统计数据 */
    @GetMapping("/stats")
    public Result<StatsVO> stats() {
        return Result.ok(goodsService.stats());
    }
}
