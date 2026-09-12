package com.travel.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.travel.common.Result;
import com.travel.dto.HotelReviewDTO;
import com.travel.entity.HotelOrder;
import com.travel.entity.HotelReview;
import com.travel.entity.Order;
import com.travel.entity.User;
import com.travel.service.impl.HotelReviewServiceImpl;
import com.travel.service.impl.OrderServiceImpl;
import com.travel.service.impl.UserServiceImpl;
import com.travel.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class HotelReviewController {

    private final HotelReviewServiceImpl reviewService;
    private final OrderServiceImpl orderService;
    private final UserServiceImpl userService;
    private final JwtUtils jwtUtils;

    // 从Token提取用户ID
    private Long getUserIdFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        try {
            String token = authHeader.substring(7);
            return jwtUtils.getUserIdFromToken(token);
        } catch (Exception e) {
            return null;
        }
    }

    // =========================================
    // ===== 提交评价 =====
    // =========================================

    @PostMapping("/submit")
    public Result<Boolean> submit(
            @RequestBody HotelReviewDTO dto,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        Long userId = getUserIdFromToken(authHeader);
        if (userId == null) {
            return Result.error("请先登录");
        }

        // 查询订单（统一订单表）
        Order order = orderService.getById(dto.getOrderId());
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (order.getStatus() != 3) {
            return Result.error("只有已完成的订单才能评价");
        }

        // 检查是否已评价
        LambdaQueryWrapper<HotelReview> checkWrapper = new LambdaQueryWrapper<>();
        checkWrapper.eq(HotelReview::getOrderId, dto.getOrderId());
        if (reviewService.count(checkWrapper) > 0) {
            return Result.error("该订单已评价");
        }

        // 获取用户信息
        User user = userService.getById(userId);

        HotelReview review = new HotelReview();
        review.setOrderId(dto.getOrderId());
        review.setOrderType(order.getOrderType());  // 从订单获取类型
        review.setHotelId(order.getTargetId());     // 从订单获取目标ID
        review.setUserId(userId);
        review.setUsername(user != null ? user.getUsername() : "匿名用户");
        review.setAvatar(user != null ? user.getAvatar() : null);
        review.setRating(dto.getRating());
        review.setContent(dto.getContent());
        review.setImages(dto.getImages());
        review.setStatus(1);

        return Result.success(reviewService.save(review));
    }

    // =========================================
    // ===== 获取某目标的评价（通用） =====
    // =========================================

    @GetMapping("/target/{orderType}/{targetId}")
    public Result<List<HotelReview>> getByTarget(
            @PathVariable String orderType,
            @PathVariable Long targetId) {
        LambdaQueryWrapper<HotelReview> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HotelReview::getOrderType, orderType)
                .eq(HotelReview::getHotelId, targetId)
                .eq(HotelReview::getStatus, 1)
                .orderByDesc(HotelReview::getCreateTime);
        return Result.success(reviewService.list(wrapper));
    }

    // =========================================
    // ===== 兼容旧的酒店评价接口 =====
    // =========================================

    @GetMapping("/hotel/{hotelId}")
    public Result<List<HotelReview>> getByHotel(@PathVariable Long hotelId) {
        return getByTarget("hotel", hotelId);
    }

    // 酒店评价统计
    @GetMapping("/hotel/{hotelId}/stats")
    public Result<Map<String, Object>> getHotelStats(@PathVariable Long hotelId) {
        return getStatsByTarget("hotel", hotelId);
    }

    // =========================================
    // ===== 通用评价统计 =====
    // =========================================

    @GetMapping("/target/{orderType}/{targetId}/stats")
    public Result<Map<String, Object>> getStatsByTarget(
            @PathVariable String orderType,
            @PathVariable Long targetId) {
        return getStatsByTargetInternal(orderType, targetId);
    }

    private Result<Map<String, Object>> getStatsByTargetInternal(String orderType, Long targetId) {
        Map<String, Object> stats = new HashMap<>();

        LambdaQueryWrapper<HotelReview> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HotelReview::getOrderType, orderType)
                .eq(HotelReview::getHotelId, targetId)
                .eq(HotelReview::getStatus, 1);

        List<HotelReview> reviews = reviewService.list(wrapper);

        long total = reviews.size();
        double avgRating = 0;
        if (total > 0) {
            avgRating = reviews.stream()
                    .mapToInt(HotelReview::getRating)
                    .average()
                    .orElse(0);
            avgRating = Math.round(avgRating * 10) / 10.0;
        }

        int[] ratingCounts = new int[6];
        for (HotelReview review : reviews) {
            int r = review.getRating();
            if (r >= 1 && r <= 5) {
                ratingCounts[r]++;
            }
        }

        stats.put("total", total);
        stats.put("avgRating", avgRating);
        stats.put("ratingCounts", ratingCounts);

        return Result.success(stats);
    }

    // =========================================
    // ===== 获取当前用户的所有评价 =====
    // =========================================

    @GetMapping("/user")
    public Result<List<Map<String, Object>>> getByUser(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        Long userId = getUserIdFromToken(authHeader);
        if (userId == null) {
            return Result.error("请先登录");
        }

        LambdaQueryWrapper<HotelReview> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HotelReview::getUserId, userId)
                .orderByDesc(HotelReview::getCreateTime);
        List<HotelReview> reviews = reviewService.list(wrapper);

        // 关联订单信息
        List<Map<String, Object>> result = new ArrayList<>();
        for (HotelReview review : reviews) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", review.getId());
            item.put("orderId", review.getOrderId());
            item.put("orderType", review.getOrderType());
            item.put("targetId", review.getHotelId());
            item.put("rating", review.getRating());
            item.put("content", review.getContent());
            item.put("createTime", review.getCreateTime());

            // 查询订单信息
            if (review.getOrderId() != null) {
                Order order = orderService.getById(review.getOrderId());
                if (order != null) {
                    item.put("orderNo", order.getOrderNo());
                    item.put("targetName", order.getTargetName());
                }
            }
            result.add(item);
        }

        return Result.success(result);
    }

    // =========================================
    // ===== 删除评价（用户） =====
    // =========================================

    @DeleteMapping("/delete/{id}")
    public Result<Boolean> delete(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        Long userId = getUserIdFromToken(authHeader);
        if (userId == null) {
            return Result.error("请先登录");
        }

        HotelReview review = reviewService.getById(id);
        if (review == null) {
            return Result.error("评价不存在");
        }
        if (!review.getUserId().equals(userId)) {
            return Result.error("无权删除该评价");
        }

        return Result.success(reviewService.removeById(id));
    }

    // =========================================
    // ===== 管理后台接口 =====
    // =========================================

    @GetMapping("/admin/list")
    public Result<List<HotelReview>> adminList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long hotelId) {

        LambdaQueryWrapper<HotelReview> wrapper = new LambdaQueryWrapper<>();

        if (hotelId != null) {
            wrapper.eq(HotelReview::getHotelId, hotelId);
        }

        if (StringUtils.hasText(keyword)) {
            wrapper.and(w ->
                    w.like(HotelReview::getContent, keyword)
                            .or()
                            .like(HotelReview::getUsername, keyword)
            );
        }

        wrapper.orderByDesc(HotelReview::getCreateTime);
        return Result.success(reviewService.list(wrapper));
    }

    @PutMapping("/admin/status/{id}")
    public Result<Boolean> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        HotelReview review = reviewService.getById(id);
        if (review == null) {
            return Result.error("评价不存在");
        }
        review.setStatus(status);
        return Result.success(reviewService.updateById(review));
    }

    @DeleteMapping("/admin/delete/{id}")
    public Result<Boolean> deleteByAdmin(@PathVariable Long id) {
        return Result.success(reviewService.removeById(id));
    }
}