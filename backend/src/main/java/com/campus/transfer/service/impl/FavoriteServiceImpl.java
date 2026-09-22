package com.campus.transfer.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.transfer.common.UserContext;
import com.campus.transfer.dto.GoodsVO;
import com.campus.transfer.entity.Category;
import com.campus.transfer.entity.Favorite;
import com.campus.transfer.entity.Goods;
import com.campus.transfer.entity.User;
import com.campus.transfer.mapper.CategoryMapper;
import com.campus.transfer.mapper.FavoriteMapper;
import com.campus.transfer.mapper.GoodsMapper;
import com.campus.transfer.mapper.UserMapper;
import com.campus.transfer.service.FavoriteService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    @Resource
    private FavoriteMapper favoriteMapper;
    @Resource
    private GoodsMapper goodsMapper;
    @Resource
    private CategoryMapper categoryMapper;
    @Resource
    private UserMapper userMapper;

    @Override
    public void add(Long goodsId) {
        Favorite favorite = new Favorite();
        favorite.setUserId(UserContext.getUserId());
        favorite.setGoodsId(goodsId);
        favorite.setCreateTime(new Date());
        favoriteMapper.insert(favorite);
    }

    @Override
    public void remove(Long goodsId) {
        favoriteMapper.delete(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, UserContext.getUserId())
                .eq(Favorite::getGoodsId, goodsId));
    }

    @Override
    public List<GoodsVO> listByUser() {
        List<Favorite> favorites = favoriteMapper.selectList(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, UserContext.getUserId())
                .orderByDesc(Favorite::getCreateTime));
        return favorites.stream()
                .map(Favorite::getGoodsId)
                .map(goodsMapper::selectById)
                .filter(g -> g != null)
                .map(this::toVO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isFavorite(Long userId, Long goodsId) {
        Long count = favoriteMapper.selectCount(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId).eq(Favorite::getGoodsId, goodsId));
        return count != null && count > 0;
    }

    private GoodsVO toVO(Goods g) {
        GoodsVO vo = new GoodsVO();
        BeanUtil.copyProperties(g, vo);
        Category category = categoryMapper.selectById(g.getCategoryId());
        if (category != null) {
            vo.setCategoryName(category.getName());
        }
        User owner = userMapper.selectById(g.getUserId());
        if (owner != null) {
            vo.setOwnerNickname(owner.getNickname());
        }
        vo.setFavorite(true);
        return vo;
    }
}
