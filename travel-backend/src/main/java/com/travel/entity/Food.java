package com.travel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("foods")
public class Food {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;          // 美食名称
    private String category;      // 分类（川菜/粤菜/小吃/饮品）
    private String cuisine;       // 菜系
    private String description;   // 描述
    private BigDecimal price;     // 参考价格
    private String address;       // 地址
    private BigDecimal rating;    // 评分
    private String images;        // 图片列表(JSON)
    private String tags;          // 标签（网红店/老字号）
    private String businessHours; // 营业时间
    private String contact;       // 联系方式
    private String city;          // 所在城市
    private Integer status;       // 状态：0下架 1上架

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}