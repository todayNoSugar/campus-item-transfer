package com.campus.transfer.service;

import com.campus.transfer.dto.GoodsVO;

import java.util.List;

public interface FavoriteService {

    /** 收藏物品 */
    void add(Long goodsId);

    /** 取消收藏 */
    void remove(Long goodsId);

    /** 我收藏的物品 */
    List<GoodsVO> listByUser();

    /** 当前用户是否已收藏 */
    boolean isFavorite(Long userId, Long goodsId);
}
