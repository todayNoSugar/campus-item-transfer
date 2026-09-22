package com.campus.transfer.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.transfer.common.UserContext;
import com.campus.transfer.dto.GoodsQuery;
import com.campus.transfer.dto.GoodsRequest;
import com.campus.transfer.dto.GoodsVO;
import com.campus.transfer.dto.StatsVO;
import com.campus.transfer.entity.Category;
import com.campus.transfer.entity.Goods;
import com.campus.transfer.entity.User;
import com.campus.transfer.mapper.CategoryMapper;
import com.campus.transfer.mapper.GoodsMapper;
import com.campus.transfer.mapper.UserMapper;
import com.campus.transfer.service.GoodsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Service
public class GoodsServiceImpl implements GoodsService {

    @Resource
    private GoodsMapper goodsMapper;
    @Resource
    private CategoryMapper categoryMapper;
    @Resource
    private UserMapper userMapper;

    @Override
    public IPage<GoodsVO> page(GoodsQuery query, boolean isAdmin) {
        LambdaQueryWrapper<Goods> wrapper = new LambdaQueryWrapper<>();
        if (!isAdmin) {
            wrapper.eq(Goods::getStatus, 1);
        } else if (query.getStatus() != null) {
            wrapper.eq(Goods::getStatus, query.getStatus());
        }
        if (StrUtil.isNotBlank(query.getKeyword())) {
            wrapper.like(Goods::getTitle, query.getKeyword());
        }
        if (query.getCategoryId() != null) {
            wrapper.eq(Goods::getCategoryId, query.getCategoryId());
        }
        if (query.getType() != null) {
            wrapper.eq(Goods::getType, query.getType());
        }
        wrapper.orderByDesc(Goods::getCreateTime);

        IPage<Goods> page = goodsMapper.selectPage(new Page<>(query.getPage(), query.getSize()), wrapper);
        return page.convert(this::toVO);
    }

    @Override
    public GoodsVO getDetail(Long id) {
        Goods goods = goodsMapper.selectById(id);
        if (goods == null) {
            return null;
        }
        return toVO(goods);
    }

    @Override
    public void create(GoodsRequest request) {
        Goods goods = new Goods();
        BeanUtil.copyProperties(request, goods);
        goods.setUserId(UserContext.getUserId());
        goods.setStatus(0);
        goods.setViewCount(0);
        goods.setCreateTime(new Date());
        goodsMapper.insert(goods);
    }

    @Override
    public void update(Long id, GoodsRequest request) {
        Goods goods = goodsMapper.selectById(id);
        if (goods == null) {
            return;
        }
        goods.setTitle(request.getTitle());
        goods.setDescription(request.getDescription());
        goods.setCategoryId(request.getCategoryId());
        goods.setImages(request.getImages());
        goods.setPrice(request.getPrice());
        goods.setType(request.getType());
        goods.setLocation(request.getLocation());
        goodsMapper.updateById(goods);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        Goods goods = goodsMapper.selectById(id);
        if (goods == null) {
            return;
        }
        goods.setStatus(status);
        goodsMapper.updateById(goods);
    }

    @Override
    public void delete(Long id) {
        goodsMapper.deleteById(id);
    }

    @Override
    public IPage<GoodsVO> listMy(Integer page, Integer size) {
        LambdaQueryWrapper<Goods> wrapper = new LambdaQueryWrapper<Goods>()
                .eq(Goods::getUserId, UserContext.getUserId())
                .orderByDesc(Goods::getCreateTime);
        return goodsMapper.selectPage(new Page<>(page, size), wrapper).convert(this::toVO);
    }

    @Override
    public void audit(Long id, Integer status) {
        Goods goods = goodsMapper.selectById(id);
        if (goods == null) {
            return;
        }
        goods.setStatus(status);
        goodsMapper.updateById(goods);
    }

    @Override
    public StatsVO stats() {
        return new StatsVO();
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
        return vo;
    }
}
