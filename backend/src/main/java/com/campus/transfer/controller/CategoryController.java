package com.campus.transfer.controller;

import com.campus.transfer.common.NoAuth;
import com.campus.transfer.common.Result;
import com.campus.transfer.entity.Category;
import com.campus.transfer.service.CategoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 分类接口
 */
@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    @NoAuth
    @GetMapping("/list")
    public Result<List<Category>> list() {
        return Result.ok(categoryService.list());
    }
}
