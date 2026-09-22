package com.campus.transfer.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.campus.transfer.dto.MessageVO;

import java.util.List;

public interface MessageService {

    /** 某物品的全部留言 */
    List<MessageVO> listByGoods(Long goodsId);

    /** 留言 */
    void add(Long goodsId, String content);

    /** 发布者回复留言 */
    void reply(Long messageId, String reply);

    /** 删除留言（留言人、发布者、管理员） */
    void delete(Long messageId);

    /** 管理员：分页查看全部留言 */
    IPage<MessageVO> pageAll(Integer page, Integer size, String keyword);
}
