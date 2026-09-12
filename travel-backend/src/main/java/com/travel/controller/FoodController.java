package com.travel.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.travel.common.Result;
import com.travel.entity.Food;
import com.travel.service.impl.FoodServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/food")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FoodController {

    private final FoodServiceImpl foodService;

    // =========================================
    // ===== 用户端接口 =====
    // =========================================

    // 获取所有美食
    @GetMapping("/list")
    public Result<List<Food>> getList() {
        LambdaQueryWrapper<Food> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Food::getStatus, 1)
                .orderByDesc(Food::getRating);
        return Result.success(foodService.list(wrapper));
    }

    // 搜索美食
    @GetMapping("/search")
    public Result<List<Food>> search(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String keyword) {

        LambdaQueryWrapper<Food> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Food::getStatus, 1);

        if (StringUtils.hasText(category)) {
            wrapper.eq(Food::getCategory, category);
        }

        if (StringUtils.hasText(city)) {
            wrapper.eq(Food::getCity, city);
        }

        if (StringUtils.hasText(keyword)) {
            wrapper.and(w ->
                    w.like(Food::getName, keyword)
                            .or()
                            .like(Food::getCity, keyword)
                            .or()
                            .like(Food::getDescription, keyword)
            );
        }

        wrapper.orderByDesc(Food::getRating);
        return Result.success(foodService.list(wrapper));
    }

    // 获取美食详情
    @GetMapping("/detail/{id}")
    public Result<Food> getDetail(@PathVariable Long id) {
        return Result.success(foodService.getById(id));
    }

    // 获取特价美食
    @GetMapping("/discount")
    public Result<List<Food>> getDiscountFood() {
        LambdaQueryWrapper<Food> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Food::getStatus, 1)
                .lt(Food::getPrice, 80)
                .orderByDesc(Food::getRating)
                .last("LIMIT 4");
        return Result.success(foodService.list(wrapper));
    }

    // 获取热门美食
    @GetMapping("/hot")
    public Result<List<Food>> getHotFood() {
        LambdaQueryWrapper<Food> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Food::getStatus, 1)
                .orderByDesc(Food::getRating)
                .last("LIMIT 4");
        return Result.success(foodService.list(wrapper));
    }

    // 获取美食统计数据
    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        Map<String, Object> stats = new java.util.HashMap<>();

        long totalFood = foodService.count();
        stats.put("totalFood", totalFood);

        QueryWrapper<Food> cityWrapper = new QueryWrapper<>();
        cityWrapper.select("city, COUNT(*) as count")
                .groupBy("city");
        List<Map<String, Object>> cityStats = foodService.listMaps(cityWrapper);
        stats.put("cityStats", cityStats);

        QueryWrapper<Food> categoryWrapper = new QueryWrapper<>();
        categoryWrapper.select("category, COUNT(*) as count")
                .groupBy("category");
        List<Map<String, Object>> categoryStats = foodService.listMaps(categoryWrapper);
        stats.put("categoryStats", categoryStats);

        return Result.success(stats);
    }

    // =========================================
    // ===== 管理后台接口 =====
    // =========================================

    // 获取所有美食（管理员，含搜索）
    @GetMapping("/admin/list")
    public Result<List<Food>> adminList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String city) {

        LambdaQueryWrapper<Food> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(city)) {
            wrapper.eq(Food::getCity, city);
        }

        if (StringUtils.hasText(keyword)) {
            wrapper.and(w ->
                    w.like(Food::getName, keyword)
                            .or()
                            .like(Food::getAddress, keyword)
                            .or()
                            .like(Food::getDescription, keyword)
            );
        }

        wrapper.orderByDesc(Food::getCreateTime);
        return Result.success(foodService.list(wrapper));
    }

    // 添加美食
    @PostMapping("/add")
    public Result<Boolean> add(@RequestBody Food food) {
        food.setId(null);
        food.setStatus(1);
        return Result.success(foodService.save(food));
    }

    // 更新美食
    @PutMapping("/update")
    public Result<Boolean> update(@RequestBody Food food) {
        return Result.success(foodService.updateById(food));
    }

    // 删除美食
    @DeleteMapping("/delete/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(foodService.removeById(id));
    }

    // 获取美食城市列表
    @GetMapping("/cities")
    public Result<List<String>> getCities() {
        QueryWrapper<Food> wrapper = new QueryWrapper<>();
        wrapper.select("DISTINCT city")
                .orderByAsc("city");
        List<Map<String, Object>> result = foodService.listMaps(wrapper);
        List<String> cities = result.stream()
                .map(m -> (String) m.get("city"))
                .filter(city -> city != null && !city.isEmpty())
                .collect(java.util.stream.Collectors.toList());
        return Result.success(cities);
    }
}