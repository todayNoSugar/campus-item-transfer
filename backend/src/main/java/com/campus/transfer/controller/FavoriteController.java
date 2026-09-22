package com.campus.transfer.controller;

import com.campus.transfer.common.Result;
import com.campus.transfer.dto.GoodsVO;
import com.campus.transfer.service.FavoriteService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 收藏接口
 */
@RestController
@RequestMapping("/api")
public class FavoriteController {

    @Resource
    private FavoriteService favoriteService;

    /** 收藏物品 */
    @PostMapping("/goods/{goodsId}/favorite")
    public Result<Void> add(@PathVariable Long goodsId) {
        favoriteService.add(goodsId);
        return Result.ok();
    }

    /** 取消收藏 */
    @DeleteMapping("/goods/{goodsId}/favorite")
    public Result<Void> remove(@PathVariable Long goodsId) {
        favoriteService.remove(goodsId);
        return Result.ok();
    }

    /** 我收藏的物品 */
    @GetMapping("/favorite/list")
    public Result<List<GoodsVO>> list() {
        return Result.ok(favoriteService.listByUser());
    }
}
