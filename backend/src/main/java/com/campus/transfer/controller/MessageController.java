package com.campus.transfer.controller;

import com.campus.transfer.common.NoAuth;
import com.campus.transfer.common.Result;
import com.campus.transfer.dto.MessageVO;
import com.campus.transfer.dto.ReplyRequest;
import com.campus.transfer.service.MessageService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 留言接口
 */
@RestController
@RequestMapping("/api")
public class MessageController {

    @Resource
    private MessageService messageService;

    /** 某物品的留言列表（游客可访问） */
    @NoAuth
    @GetMapping("/goods/{goodsId}/messages")
    public Result<List<MessageVO>> list(@PathVariable Long goodsId) {
        return Result.ok(messageService.listByGoods(goodsId));
    }

    /** 留言 */
    @PostMapping("/goods/{goodsId}/message")
    public Result<Void> add(@PathVariable Long goodsId, @RequestBody Map<String, String> body) {
        messageService.add(goodsId, body.get("content"));
        return Result.ok();
    }

    /** 发布者回复留言 */
    @PutMapping("/message/{id}/reply")
    public Result<Void> reply(@PathVariable Long id, @RequestBody ReplyRequest request) {
        messageService.reply(id, request.getReply());
        return Result.ok();
    }

    /** 删除留言（留言人、发布者、管理员） */
    @DeleteMapping("/message/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        messageService.delete(id);
        return Result.ok();
    }
}
