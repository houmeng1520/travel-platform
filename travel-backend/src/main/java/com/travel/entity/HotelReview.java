package com.travel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("hotel_reviews")
public class HotelReview {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long orderId;
    private String orderType;  // 新增：hotel/scenic/food/route
    private Long hotelId;      // 通用目标ID（酒店/景点/美食/路线）
    private Long userId;
    private String username;
    private String avatar;
    private Integer rating;
    private String content;
    private String images;
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}