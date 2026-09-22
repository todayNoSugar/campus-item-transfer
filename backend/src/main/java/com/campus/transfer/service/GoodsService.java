package com.campus.transfer.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.campus.transfer.dto.GoodsQuery;
import com.campus.transfer.dto.GoodsRequest;
import com.campus.transfer.dto.GoodsVO;
import com.campus.transfer.dto.StatsVO;

public interface GoodsService {

    /** 分页查询物品列表（isAdmin=true 时不过滤状态，用于管理后台） */
    IPage<GoodsVO> page(GoodsQuery query, boolean isAdmin);

    /** 物品详情（浏览数+1） */
    GoodsVO getDetail(Long id);

    /** 发布物品（默认待审核） */
    void create(GoodsRequest request);

    /** 修改物品（仅发布者本人） */
    void update(Long id, GoodsRequest request);

    /** 变更物品状态：1上架 2已成交 3下架（仅发布者本人） */
    void updateStatus(Long id, Integer status);

    /** 删除物品（仅发布者本人，级联删除留言/预约/收藏） */
    void delete(Long id);

    /** 我发布的物品 */
    IPage<GoodsVO> listMy(Integer page, Integer size);

    /** 管理员：审核物品，status=1通过上架 / 3拒绝下架 */
    void audit(Long id, Integer status);

    /** 管理端数据看板统计 */
    StatsVO stats();
}
