package com.campus.transfer.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.campus.transfer.common.BusinessException;
import com.campus.transfer.common.Result;
import com.campus.transfer.config.AppProperties;
import com.campus.transfer.dto.ProfileRequest;
import com.campus.transfer.service.UserService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 个人中心与文件上传接口
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    /** 允许上传的图片扩展名 */
    private static final List<String> ALLOWED_EXT = Arrays.asList(".jpg", ".jpeg", ".png", ".gif", ".webp", ".bmp");

    @Resource
    private UserService userService;

    @Resource
    private AppProperties props;

    /** 修改个人资料（昵称、头像、手机号） */
    @PutMapping("/profile")
    public Result<Void> updateProfile(@RequestBody ProfileRequest request) {
        userService.updateProfile(request);
        return Result.ok();
    }

    /** 图片上传（物品图片、头像通用），返回可访问的图片地址 */
    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("请选择要上传的文件");
        }
        String original = file.getOriginalFilename();
        String ext = StrUtil.isBlank(original) || !original.contains(".")
                ? ".jpg"
                : original.substring(original.lastIndexOf(".")).toLowerCase();
        if (!ALLOWED_EXT.contains(ext)) {
            throw new BusinessException("仅支持图片格式：" + String.join(" ", ALLOWED_EXT));
        }
        try {
            String dirPath = new File(System.getProperty("user.dir"), props.getUpload().getDir()).getAbsolutePath();
            File dir = new File(dirPath);
            if (!dir.exists() && !dir.mkdirs()) {
                throw new BusinessException("上传目录创建失败");
            }
            String fileName = DateUtil.format(new Date(), "yyyyMMddHHmmss") + RandomUtil.randomNumbers(6) + ext;
            file.transferTo(new File(dir, fileName));
            return Result.ok("/upload/" + fileName);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("图片上传失败：" + e.getMessage());
        }
    }
}
