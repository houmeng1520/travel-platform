package com.travel.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.travel.common.Result;
import com.travel.dto.HotelOrderDTO;
import com.travel.entity.Hotel;
import com.travel.entity.HotelOrder;
import com.travel.service.impl.HotelOrderServiceImpl;
import com.travel.service.impl.HotelServiceImpl;
import com.travel.utils.JwtUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/hotel-order")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class HotelOrderController {

    private final HotelOrderServiceImpl hotelOrderService;
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
    // ===== 用户端接口 =====
    // =========================================

    // 创建预订
    @PostMapping("/create")
    public Result<HotelOrder> create(
            @Valid @RequestBody HotelOrderDTO dto,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        Long userId = getUserIdFromToken(authHeader);
        if (userId == null) {
            return Result.error("请先登录");
        }

        Hotel hotel = hotelService.getById(dto.getHotelId());
        if (hotel == null) {
            return Result.error("酒店不存在");
        }

        LocalDate checkIn = dto.getCheckInDate();
        LocalDate checkOut = dto.getCheckOutDate();
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        if (nights <= 0) {
            return Result.error("离店日期必须晚于入住日期");
        }

        int quantity = dto.getQuantity() != null ? dto.getQuantity() : 1;
        BigDecimal total = hotel.getPrice().multiply(BigDecimal.valueOf(nights)).multiply(BigDecimal.valueOf(quantity));

        String orderNo = "H" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        HotelOrder order = new HotelOrder();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setHotelId(hotel.getId());
        order.setHotelName(hotel.getName());
        order.setRoomType(dto.getRoomType() != null ? dto.getRoomType() : "标准间");
        order.setCheckInDate(checkIn);
        order.setCheckOutDate(checkOut);
        order.setNights((int) nights);
        order.setQuantity(quantity);
        order.setPrice(hotel.getPrice());
        order.setTotalAmount(total);
        order.setGuestName(dto.getGuestName());
        order.setGuestPhone(dto.getGuestPhone());
        order.setGuestEmail(dto.getGuestEmail());
        order.setRemark(dto.getRemark());
        order.setStatus(0);  // 待支付

        hotelOrderService.save(order);
        return Result.success(order);
    }

    // 获取我的预订
    @GetMapping("/my-orders")
    public Result<List<HotelOrder>> getMyOrders(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        Long userId = getUserIdFromToken(authHeader);
        if (userId == null) {
            return Result.error("请先登录");
        }

        LambdaQueryWrapper<HotelOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HotelOrder::getUserId, userId)
                .orderByDesc(HotelOrder::getCreateTime);
        return Result.success(hotelOrderService.list(wrapper));
    }

    // 获取预订详情
    @GetMapping("/detail/{id}")
    public Result<HotelOrder> getDetail(@PathVariable Long id) {
        HotelOrder order = hotelOrderService.getById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        return Result.success(order);
    }

    // 取消预订（用户端）
    @PutMapping("/cancel/{id}")
    public Result<Boolean> cancel(@PathVariable Long id) {
        HotelOrder order = hotelOrderService.getById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (order.getStatus() >= 3) {
            return Result.error("该订单无法取消");
        }
        order.setStatus(4);
        return Result.success(hotelOrderService.updateById(order));
    }

    // =========================================
    // ===== 支付接口 =====
    // =========================================

    // 支付订单（修复版）
    @PutMapping("/pay/{id}")
    public Result<Boolean> pay(@PathVariable Long id) {
        HotelOrder order = hotelOrderService.getById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (order.getStatus() != 0) {
            return Result.error("该订单无法支付，当前状态：" + order.getStatus());
        }
        order.setStatus(1);  // 已确认/已支付
        boolean result = hotelOrderService.updateById(order);
        System.out.println("支付结果: " + result + ", 订单ID: " + id + ", 状态变更为: 1");
        return Result.success(result);
    }

    // =========================================
    // ===== 管理后台接口 =====
    // =========================================

    @GetMapping("/admin/list")
    public Result<List<HotelOrder>> adminList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {

        LambdaQueryWrapper<HotelOrder> wrapper = new LambdaQueryWrapper<>();

        if (status != null) {
            wrapper.eq(HotelOrder::getStatus, status);
        }

        if (StringUtils.hasText(keyword)) {
            wrapper.and(w ->
                    w.like(HotelOrder::getOrderNo, keyword)
                            .or()
                            .like(HotelOrder::getHotelName, keyword)
                            .or()
                            .like(HotelOrder::getGuestName, keyword)
            );
        }

        wrapper.orderByDesc(HotelOrder::getCreateTime);
        return Result.success(hotelOrderService.list(wrapper));
    }

    @PutMapping("/confirm/{id}")
    public Result<Boolean> confirm(@PathVariable Long id) {
        HotelOrder order = hotelOrderService.getById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (order.getStatus() != 0) {
            return Result.error("只有待支付的订单才能确认");
        }
        order.setStatus(1);
        return Result.success(hotelOrderService.updateById(order));
    }

    @PutMapping("/checkin/{id}")
    public Result<Boolean> checkin(@PathVariable Long id) {
        HotelOrder order = hotelOrderService.getById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (order.getStatus() != 1) {
            return Result.error("只有已支付的订单才能办理入住");
        }
        order.setStatus(2);
        return Result.success(hotelOrderService.updateById(order));
    }

    @PutMapping("/complete/{id}")
    public Result<Boolean> complete(@PathVariable Long id) {
        HotelOrder order = hotelOrderService.getById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (order.getStatus() != 2) {
            return Result.error("只有已入住的订单才能完成");
        }
        order.setStatus(3);
        return Result.success(hotelOrderService.updateById(order));
    }

    @PutMapping("/admin/cancel/{id}")
    public Result<Boolean> adminCancel(@PathVariable Long id) {
        HotelOrder order = hotelOrderService.getById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (order.getStatus() >= 3) {
            return Result.error("该订单无法取消");
        }
        order.setStatus(4);
        return Result.success(hotelOrderService.updateById(order));
    }
}