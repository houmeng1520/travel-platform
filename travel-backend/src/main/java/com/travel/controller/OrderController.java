package com.travel.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.travel.common.Result;
import com.travel.dto.OrderDTO;
import com.travel.entity.*;
import com.travel.service.impl.*;
import com.travel.utils.JwtUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderServiceImpl orderService;
    private final ScenicServiceImpl scenicService;
    private final FoodServiceImpl foodService;
    private final RouteServiceImpl routeService;
    private final HotelServiceImpl hotelService;
    private final JwtUtils jwtUtils;

    // ===== 从Token提取用户ID =====
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
    // ===== 创建订单 =====
    // =========================================

    @PostMapping("/create")
    public Result<Order> create(
            @Valid @RequestBody OrderDTO dto,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        Long userId = getUserIdFromToken(authHeader);
        if (userId == null) {
            return Result.error("请先登录");
        }

        Order order = new Order();
        order.setOrderNo("O" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        order.setUserId(userId);
        order.setOrderType(dto.getOrderType());
        order.setTargetId(dto.getTargetId());
        order.setQuantity(dto.getQuantity() != null ? dto.getQuantity() : 1);
        order.setNights(dto.getNights() != null ? dto.getNights() : 1);
        order.setOrderDate(dto.getOrderDate());
        order.setEndDate(dto.getEndDate());
        order.setGuestName(dto.getGuestName());
        order.setGuestPhone(dto.getGuestPhone());
        order.setGuestEmail(dto.getGuestEmail());
        order.setRemark(dto.getRemark());
        order.setStatus(0);  // 待支付

        // 根据类型获取价格和名称
        BigDecimal unitPrice = BigDecimal.ZERO;
        String targetName = "";

        switch (dto.getOrderType()) {
            case "scenic":
                Scenic scenic = scenicService.getById(dto.getTargetId());
                if (scenic == null) return Result.error("景点不存在");
                targetName = scenic.getName();
                unitPrice = scenic.getPrice();
                order.setOrderDate(LocalDate.now());
                break;

            case "food":
                Food food = foodService.getById(dto.getTargetId());
                if (food == null) return Result.error("美食不存在");
                targetName = food.getName();
                unitPrice = food.getPrice();
                order.setOrderDate(LocalDate.now());
                break;

            case "route":
                Route route = routeService.getById(dto.getTargetId());
                if (route == null) return Result.error("路线不存在");
                targetName = route.getTitle();
                unitPrice = route.getPrice();
                order.setOrderDate(LocalDate.now());
                break;

            case "hotel":
                Hotel hotel = hotelService.getById(dto.getTargetId());
                if (hotel == null) return Result.error("酒店不存在");
                targetName = hotel.getName();
                unitPrice = hotel.getPrice();
                if (dto.getOrderDate() == null || dto.getEndDate() == null) {
                    return Result.error("请选择入住和离店日期");
                }
                long nights = ChronoUnit.DAYS.between(dto.getOrderDate(), dto.getEndDate());
                if (nights <= 0) return Result.error("离店日期必须晚于入住日期");
                order.setNights((int) nights);
                break;

            default:
                return Result.error("不支持的订单类型");
        }

        // 计算总金额
        BigDecimal total = unitPrice.multiply(BigDecimal.valueOf(order.getQuantity()));
        if ("hotel".equals(dto.getOrderType())) {
            total = total.multiply(BigDecimal.valueOf(order.getNights()));
        }

        order.setTargetName(targetName);
        order.setPrice(unitPrice);
        order.setTotalAmount(total);

        orderService.save(order);
        return Result.success(order);
    }

    // =========================================
    // ===== 获取我的订单 =====
    // =========================================

    @GetMapping("/my-orders")
    public Result<List<Order>> getMyOrders(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(required = false) String orderType) {

        Long userId = getUserIdFromToken(authHeader);
        if (userId == null) {
            return Result.error("请先登录");
        }

        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, userId)
                .orderByDesc(Order::getCreateTime);

        if (orderType != null && !orderType.isEmpty()) {
            wrapper.eq(Order::getOrderType, orderType);
        }

        return Result.success(orderService.list(wrapper));
    }

    // =========================================
    // ===== 获取订单详情 =====
    // =========================================

    @GetMapping("/detail/{id}")
    public Result<Order> getDetail(@PathVariable Long id) {
        return Result.success(orderService.getById(id));
    }

    // =========================================
    // ===== 支付订单 =====
    // =========================================

    @PutMapping("/pay/{id}")
    public Result<Boolean> pay(@PathVariable Long id) {
        Order order = orderService.getById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (order.getStatus() != 0) {
            return Result.error("该订单无法支付");
        }
        order.setStatus(1);
        order.setPayTime(java.time.LocalDateTime.now());
        return Result.success(orderService.updateById(order));
    }

    // =========================================
    // ===== 使用订单（新增） =====
    // =========================================

    // ===== 使用订单（点击后直接完成） =====
    @PutMapping("/use/{id}")
    public Result<Boolean> use(@PathVariable Long id) {
        Order order = orderService.getById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (order.getStatus() != 1) {
            return Result.error("只有已支付的订单才能使用");
        }
        order.setStatus(3);  // ← 改为 3（已完成）
        return Result.success(orderService.updateById(order));
    }

    // =========================================
    // ===== 取消订单 =====
    // =========================================

    @PutMapping("/cancel/{id}")
    public Result<Boolean> cancel(@PathVariable Long id) {
        Order order = orderService.getById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (order.getStatus() >= 3) {
            return Result.error("该订单无法取消");
        }
        order.setStatus(4);
        return Result.success(orderService.updateById(order));
    }

    // =========================================
    // ===== 管理后台接口 =====
    // =========================================

    // 获取所有订单
    @GetMapping("/admin/list")
    public Result<List<Order>> adminList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String orderType,
            @RequestParam(required = false) Integer status) {

        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();

        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }
        if (orderType != null && !orderType.isEmpty()) {
            wrapper.eq(Order::getOrderType, orderType);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w ->
                    w.like(Order::getOrderNo, keyword)
                            .or()
                            .like(Order::getTargetName, keyword)
                            .or()
                            .like(Order::getGuestName, keyword)
            );
        }

        wrapper.orderByDesc(Order::getCreateTime);
        return Result.success(orderService.list(wrapper));
    }

    // ===== 管理员：标记已使用 =====
    @PutMapping("/admin/use/{id}")
    public Result<Boolean> adminUse(@PathVariable Long id) {
        Order order = orderService.getById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (order.getStatus() != 1) {
            return Result.error("只有已支付的订单才能标记完成");
        }
        order.setStatus(3);  // ← 改为 3（已完成）
        return Result.success(orderService.updateById(order));
    }

    // ===== 管理员：标记已完成 =====
    @PutMapping("/complete/{id}")
    public Result<Boolean> complete(@PathVariable Long id) {
        Order order = orderService.getById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (order.getStatus() != 2) {
            return Result.error("只有已使用的订单才能完成");
        }
        order.setStatus(3);
        return Result.success(orderService.updateById(order));
    }

    // ===== 管理员：取消订单 =====
    @PutMapping("/admin/cancel/{id}")
    public Result<Boolean> adminCancel(@PathVariable Long id) {
        Order order = orderService.getById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (order.getStatus() >= 3) {
            return Result.error("该订单无法取消");
        }
        order.setStatus(4);
        return Result.success(orderService.updateById(order));
    }
}