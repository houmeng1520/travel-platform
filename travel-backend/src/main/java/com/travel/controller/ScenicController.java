package com.travel.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.travel.common.Result;
import com.travel.entity.Scenic;
import com.travel.service.impl.ScenicServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/scenic")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ScenicController {

    private final ScenicServiceImpl scenicService;

    // ===== 用户端接口 =====

    @GetMapping("/list")
    public Result<List<Scenic>> getList() {
        return Result.success(scenicService.list());
    }

    @GetMapping("/search")
    public Result<List<Scenic>> search(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword) {

        LambdaQueryWrapper<Scenic> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(city)) {
            wrapper.eq(Scenic::getCity, city);
        }

        if (StringUtils.hasText(category)) {
            wrapper.eq(Scenic::getCategory, category);
        }

        if (StringUtils.hasText(keyword)) {
            wrapper.and(w ->
                    w.like(Scenic::getName, keyword)
                            .or()
                            .like(Scenic::getCity, keyword)
                            .or()
                            .like(Scenic::getAddress, keyword)
                            .or()
                            .like(Scenic::getDescription, keyword)
            );
        }

        wrapper.orderByDesc(Scenic::getRating);
        return Result.success(scenicService.list(wrapper));
    }

    @GetMapping("/detail/{id}")
    public Result<Scenic> getDetail(@PathVariable Long id) {
        return Result.success(scenicService.getById(id));
    }

    @GetMapping("/discount")
    public Result<List<Scenic>> getDiscountScenic() {
        LambdaQueryWrapper<Scenic> wrapper = new LambdaQueryWrapper<>();
        wrapper.lt(Scenic::getPrice, 100)
                .orderByDesc(Scenic::getRating)
                .last("LIMIT 4");
        return Result.success(scenicService.list(wrapper));
    }

    @GetMapping("/hot")
    public Result<List<Scenic>> getHotScenic() {
        LambdaQueryWrapper<Scenic> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Scenic::getRating)
                .last("LIMIT 4");
        return Result.success(scenicService.list(wrapper));
    }

    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        Map<String, Object> stats = new java.util.HashMap<>();

        long totalScenic = scenicService.count();
        stats.put("totalScenic", totalScenic);

        QueryWrapper<Scenic> cityWrapper = new QueryWrapper<>();
        cityWrapper.select("city, COUNT(*) as count")
                .groupBy("city");
        List<Map<String, Object>> cityStats = scenicService.listMaps(cityWrapper);
        stats.put("cityStats", cityStats);

        QueryWrapper<Scenic> categoryWrapper = new QueryWrapper<>();
        categoryWrapper.select("category, COUNT(*) as count")
                .groupBy("category");
        List<Map<String, Object>> categoryStats = scenicService.listMaps(categoryWrapper);
        stats.put("categoryStats", categoryStats);

        return Result.success(stats);
    }

    // ===== 管理后台接口 =====

    @GetMapping("/admin/list")
    public Result<List<Scenic>> adminList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String city) {

        LambdaQueryWrapper<Scenic> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(city)) {
            wrapper.eq(Scenic::getCity, city);
        }

        if (StringUtils.hasText(keyword)) {
            wrapper.and(w ->
                    w.like(Scenic::getName, keyword)
                            .or()
                            .like(Scenic::getAddress, keyword)
                            .or()
                            .like(Scenic::getDescription, keyword)
            );
        }

        wrapper.orderByDesc(Scenic::getCreateTime);
        return Result.success(scenicService.list(wrapper));
    }

    @PostMapping("/add")
    public Result<Boolean> add(@RequestBody Scenic scenic) {
        scenic.setId(null);
        return Result.success(scenicService.save(scenic));
    }

    @PutMapping("/update")
    public Result<Boolean> update(@RequestBody Scenic scenic) {
        return Result.success(scenicService.updateById(scenic));
    }

    @DeleteMapping("/delete/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(scenicService.removeById(id));
    }

    @GetMapping("/cities")
    public Result<List<String>> getCities() {
        QueryWrapper<Scenic> wrapper = new QueryWrapper<>();
        wrapper.select("DISTINCT city")
                .orderByAsc("city");
        List<Map<String, Object>> result = scenicService.listMaps(wrapper);
        List<String> cities = result.stream()
                .map(m -> (String) m.get("city"))
                .filter(city -> city != null && !city.isEmpty())
                .collect(java.util.stream.Collectors.toList());
        return Result.success(cities);
    }
}