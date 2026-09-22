package com.campus.transfer.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.transfer.common.UserContext;
import com.campus.transfer.dto.MessageVO;
import com.campus.transfer.entity.Message;
import com.campus.transfer.entity.User;
import com.campus.transfer.mapper.MessageMapper;
import com.campus.transfer.mapper.UserMapper;
import com.campus.transfer.service.MessageService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl implements MessageService {

    @Resource
    private MessageMapper messageMapper;
    @Resource
    private UserMapper userMapper;

    @Override
    public List<MessageVO> listByGoods(Long goodsId) {
        List<Message> list = messageMapper.selectList(new LambdaQueryWrapper<Message>()
                .eq(Message::getGoodsId, goodsId)
                .orderByAsc(Message::getCreateTime));
        return list.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public void add(Long goodsId, String content) {
        if (StrUtil.isBlank(content)) {
            return;
        }
        Message message = new Message();
        message.setGoodsId(goodsId);
        message.setUserId(UserContext.getUserId());
        message.setContent(content);
        message.setCreateTime(new Date());
        messageMapper.insert(message);
    }

    @Override
    public void reply(Long messageId, String reply) {
        Message message = messageMapper.selectById(messageId);
        if (message == null) {
            return;
        }
        message.setReply(reply);
        messageMapper.updateById(message);
    }

    @Override
    public void delete(Long messageId) {
        messageMapper.deleteById(messageId);
    }

    @Override
    public IPage<MessageVO> pageAll(Integer page, Integer size, String keyword) {
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.like(Message::getContent, keyword);
        }
        wrapper.orderByDesc(Message::getCreateTime);
        return messageMapper.selectPage(new Page<>(page, size), wrapper).convert(this::toVO);
    }

    private MessageVO toVO(Message m) {
        MessageVO vo = new MessageVO();
        vo.setId(m.getId());
        vo.setGoodsId(m.getGoodsId());
        vo.setUserId(m.getUserId());
        vo.setContent(m.getContent());
        vo.setReply(m.getReply());
        vo.setCreateTime(m.getCreateTime());
        User user = userMapper.selectById(m.getUserId());
        if (user != null) {
            vo.setNickname(user.getNickname());
        }
        return vo;
    }
}
