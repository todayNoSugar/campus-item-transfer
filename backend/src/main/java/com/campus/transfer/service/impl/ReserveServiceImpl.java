package com.campus.transfer.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.transfer.common.UserContext;
import com.campus.transfer.dto.ReserveRequest;
import com.campus.transfer.dto.ReserveVO;
import com.campus.transfer.entity.Goods;
import com.campus.transfer.entity.Reserve;
import com.campus.transfer.entity.User;
import com.campus.transfer.mapper.GoodsMapper;
import com.campus.transfer.mapper.ReserveMapper;
import com.campus.transfer.mapper.UserMapper;
import com.campus.transfer.service.ReserveService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReserveServiceImpl implements ReserveService {

    @Resource
    private ReserveMapper reserveMapper;
    @Resource
    private GoodsMapper goodsMapper;
    @Resource
    private UserMapper userMapper;

    @Override
    public void create(Long goodsId, ReserveRequest request) {
        Goods goods = goodsMapper.selectById(goodsId);
        if (goods == null) {
            return;
        }
        Reserve reserve = new Reserve();
        reserve.setGoodsId(goodsId);
        reserve.setUserId(UserContext.getUserId());
        reserve.setOwnerId(goods.getUserId());
        if (StrUtil.isNotBlank(request.getReserveTime())) {
            reserve.setReserveTime(DateUtil.parse(request.getReserveTime()));
        }
        reserve.setRemark(request.getRemark());
        reserve.setStatus(0);
        reserve.setCreateTime(new Date());
        reserveMapper.insert(reserve);
    }

    @Override
    public List<ReserveVO> listMy() {
        List<Reserve> list = reserveMapper.selectList(new LambdaQueryWrapper<Reserve>()
                .eq(Reserve::getUserId, UserContext.getUserId())
                .orderByDesc(Reserve::getCreateTime));
        return list.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public List<ReserveVO> listToMe() {
        List<Reserve> list = reserveMapper.selectList(new LambdaQueryWrapper<Reserve>()
                .eq(Reserve::getOwnerId, UserContext.getUserId())
                .orderByDesc(Reserve::getCreateTime));
        return list.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        Reserve reserve = reserveMapper.selectById(id);
        if (reserve == null) {
            return;
        }
        reserve.setStatus(status);
        reserveMapper.updateById(reserve);
    }

    private ReserveVO toVO(Reserve r) {
        ReserveVO vo = new ReserveVO();
        vo.setId(r.getId());
        vo.setGoodsId(r.getGoodsId());
        vo.setUserId(r.getUserId());
        vo.setOwnerId(r.getOwnerId());
        vo.setReserveTime(r.getReserveTime());
        vo.setStatus(r.getStatus());
        vo.setRemark(r.getRemark());
        vo.setCreateTime(r.getCreateTime());

        Goods goods = goodsMapper.selectById(r.getGoodsId());
        if (goods != null) {
            vo.setGoodsTitle(goods.getTitle());
            vo.setGoodsStatus(goods.getStatus());
        }
        User user = userMapper.selectById(r.getUserId());
        if (user != null) {
            vo.setUserName(user.getNickname());
        }
        return vo;
    }
}
