package com.travel.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.travel.common.Result;
import com.travel.entity.Hotel;
import com.travel.service.impl.HotelServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/hotel")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class HotelController {

    private final HotelServiceImpl hotelService;

    // ===== 用户端接口 =====

    // 获取所有酒店
    @GetMapping("/list")
    public Result<List<Hotel>> getList() {
        LambdaQueryWrapper<Hotel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Hotel::getStatus, 1)
                .orderByDesc(Hotel::getRating);
        return Result.success(hotelService.list(wrapper));
    }

    // 搜索酒店（支持城市、关键词、limit限制）
    @GetMapping("/search")
    public Result<List<Hotel>> search(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer limit) {

        LambdaQueryWrapper<Hotel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Hotel::getStatus, 1);

        if (StringUtils.hasText(city)) {
            wrapper.eq(Hotel::getCity, city);
        }

        if (StringUtils.hasText(keyword)) {
            wrapper.and(w ->
                    w.like(Hotel::getName, keyword)
                            .or()
                            .like(Hotel::getAddress, keyword)
                            .or()
                            .like(Hotel::getDescription, keyword)
            );
        }

        wrapper.orderByDesc(Hotel::getRating);

        if (limit != null && limit > 0) {
            wrapper.last("LIMIT " + limit);
        }

        return Result.success(hotelService.list(wrapper));
    }

    // 获取酒店详情
    @GetMapping("/detail/{id}")
    public Result<Hotel> getDetail(@PathVariable Long id) {
        return Result.success(hotelService.getById(id));
    }

    // 获取热门酒店（评分最高的4家）
    @GetMapping("/hot")
    public Result<List<Hotel>> getHot() {
        LambdaQueryWrapper<Hotel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Hotel::getStatus, 1)
                .orderByDesc(Hotel::getRating)
                .last("LIMIT 4");
        return Result.success(hotelService.list(wrapper));
    }

    // 获取酒店统计数据
    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        Map<String, Object> stats = new java.util.HashMap<>();

        long total = hotelService.count();
        stats.put("totalHotel", total);

        QueryWrapper<Hotel> cityWrapper = new QueryWrapper<>();
        cityWrapper.select("city, COUNT(*) as count")
                .groupBy("city");
        List<Map<String, Object>> cityStats = hotelService.listMaps(cityWrapper);
        stats.put("cityStats", cityStats);

        return Result.success(stats);
    }

    // ===== 管理后台接口 =====

    // 获取所有酒店（管理员，含下架）
    @GetMapping("/admin/list")
    public Result<List<Hotel>> adminList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String city) {

        LambdaQueryWrapper<Hotel> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(city)) {
            wrapper.eq(Hotel::getCity, city);
        }

        if (StringUtils.hasText(keyword)) {
            wrapper.and(w ->
                    w.like(Hotel::getName, keyword)
                            .or()
                            .like(Hotel::getAddress, keyword)
                            .or()
                            .like(Hotel::getDescription, keyword)
            );
        }

        wrapper.orderByDesc(Hotel::getCreateTime);
        return Result.success(hotelService.list(wrapper));
    }

    // 添加酒店
    @PostMapping("/add")
    public Result<Boolean> add(@RequestBody Hotel hotel) {
        hotel.setId(null);
        return Result.success(hotelService.save(hotel));
    }

    // 更新酒店
    @PutMapping("/update")
    public Result<Boolean> update(@RequestBody Hotel hotel) {
        return Result.success(hotelService.updateById(hotel));
    }

    // 删除酒店
    @DeleteMapping("/delete/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(hotelService.removeById(id));
    }
}