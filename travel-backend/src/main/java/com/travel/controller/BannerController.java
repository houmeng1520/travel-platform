package com.travel.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.travel.common.Result;
import com.travel.entity.Banner;
import com.travel.service.impl.BannerServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/banner")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BannerController {

    private final BannerServiceImpl bannerService;

    // 获取所有轮播图
    @GetMapping("/list")
    public Result<List<Banner>> getList() {
        LambdaQueryWrapper<Banner> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Banner::getSortOrder);
        return Result.success(bannerService.list(wrapper));
    }

    // 添加轮播图
    @PostMapping("/add")
    public Result<Boolean> add(@RequestBody Banner banner) {
        banner.setId(null);
        return Result.success(bannerService.save(banner));
    }

    // 更新轮播图
    @PutMapping("/update")
    public Result<Boolean> update(@RequestBody Banner banner) {
        return Result.success(bannerService.updateById(banner));
    }

    // 删除轮播图
    @DeleteMapping("/delete/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(bannerService.removeById(id));
    }
}