package com.travel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("scenic_spots")
public class Scenic {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;
    private String description;
    private String address;
    private BigDecimal price;
    private BigDecimal rating;
    private String images;
    private String category;
    private String city;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}