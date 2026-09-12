package com.travel.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.travel.common.Result;
import com.travel.entity.*;
import com.travel.mapper.RouteFoodRelMapper;
import com.travel.mapper.RouteScenicRelMapper;
import com.travel.service.impl.FoodServiceImpl;
import com.travel.service.impl.RouteServiceImpl;
import com.travel.service.impl.ScenicServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/route")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RouteController {

    private final RouteServiceImpl routeService;
    private final RouteScenicRelMapper routeScenicRelMapper;
    private final RouteFoodRelMapper routeFoodRelMapper;
    private final ScenicServiceImpl scenicService;
    private final FoodServiceImpl foodService;

    // 获取所有路线
    @GetMapping("/list")
    public Result<List<Route>> getList() {
        LambdaQueryWrapper<Route> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Route::getRating);
        return Result.success(routeService.list(wrapper));
    }

    // 搜索路线
    @GetMapping("/search")
    public Result<List<Route>> search(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String keyword) {

        LambdaQueryWrapper<Route> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(city)) {
            wrapper.eq(Route::getCity, city);
        }

        if (StringUtils.hasText(keyword)) {
            wrapper.and(w ->
                    w.like(Route::getTitle, keyword)
                            .or()
                            .like(Route::getDescription, keyword)
            );
        }

        wrapper.orderByDesc(Route::getRating);
        return Result.success(routeService.list(wrapper));
    }

    // 获取路线详情（含关联的景点和美食）
    @GetMapping("/detail/{id}")
    public Result<Map<String, Object>> getDetail(@PathVariable Long id) {
        // 1. 查询路线基本信息
        Route route = routeService.getById(id);
        if (route == null) {
            return Result.error("路线不存在");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("route", route);

        // 2. 查询关联的景点ID列表
        LambdaQueryWrapper<RouteScenicRel> scenicRelWrapper = new LambdaQueryWrapper<>();
        scenicRelWrapper.eq(RouteScenicRel::getRouteId, id)
                .orderByAsc(RouteScenicRel::getDayIndex)
                .orderByAsc(RouteScenicRel::getSortOrder);
        List<RouteScenicRel> scenicRels = routeScenicRelMapper.selectList(scenicRelWrapper);

        if (!scenicRels.isEmpty()) {
            List<Long> scenicIds = scenicRels.stream()
                    .map(RouteScenicRel::getScenicId)
                    .collect(Collectors.toList());
            List<Scenic> scenics = scenicService.listByIds(scenicIds);

            // 组装数据
            List<Map<String, Object>> scenicList = new ArrayList<>();
            for (RouteScenicRel rel : scenicRels) {
                Map<String, Object> item = new HashMap<>();
                item.put("dayIndex", rel.getDayIndex());
                item.put("stayTime", rel.getStayTime());
                scenics.stream()
                        .filter(s -> s.getId().equals(rel.getScenicId()))
                        .findFirst()
                        .ifPresent(s -> item.put("scenic", s));
                scenicList.add(item);
            }
            result.put("scenics", scenicList);
        }

        // 3. 查询关联的美食ID列表
        LambdaQueryWrapper<RouteFoodRel> foodRelWrapper = new LambdaQueryWrapper<>();
        foodRelWrapper.eq(RouteFoodRel::getRouteId, id)
                .orderByAsc(RouteFoodRel::getDayIndex);
        List<RouteFoodRel> foodRels = routeFoodRelMapper.selectList(foodRelWrapper);

        if (!foodRels.isEmpty()) {
            List<Long> foodIds = foodRels.stream()
                    .map(RouteFoodRel::getFoodId)
                    .collect(Collectors.toList());
            List<Food> foods = foodService.listByIds(foodIds);

            List<Map<String, Object>> foodList = new ArrayList<>();
            for (RouteFoodRel rel : foodRels) {
                Map<String, Object> item = new HashMap<>();
                item.put("dayIndex", rel.getDayIndex());
                item.put("mealType", rel.getMealType());
                item.put("recommendation", rel.getRecommendation());
                foods.stream()
                        .filter(f -> f.getId().equals(rel.getFoodId()))
                        .findFirst()
                        .ifPresent(f -> item.put("food", f));
                foodList.add(item);
            }
            result.put("foods", foodList);
        }

        return Result.success(result);
    }

    // ===== CRUD =====

    // 添加路线
    @PostMapping("/add")
    public Result<Boolean> add(@RequestBody Route route) {
        route.setId(null);
        route.setViewCount(0);
        return Result.success(routeService.save(route));
    }

    // 更新路线
    @PutMapping("/update")
    public Result<Boolean> update(@RequestBody Route route) {
        return Result.success(routeService.updateById(route));
    }

    // 删除路线
    @DeleteMapping("/delete/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(routeService.removeById(id));
    }
}