package com.travel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("hotels")
public class Hotel {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;
    private Integer starLevel;
    private String address;
    private BigDecimal price;
    private BigDecimal rating;
    private String images;
    private String facilities;
    private String description;
    private String city;
    private String phone;
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}