package com.travel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("routes")
public class Route {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;
    private String description;
    private String coverImage;
    private Integer days;
    private BigDecimal price;
    private BigDecimal rating;
    private Integer viewCount;
    private String city;
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}