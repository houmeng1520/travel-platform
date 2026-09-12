package com.travel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("orders")
public class Order {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String orderNo;
    private Long userId;
    private String orderType;  // hotel / scenic / food / route
    private Long targetId;
    private String targetName;
    private BigDecimal price;
    private Integer quantity;
    private Integer nights;
    private BigDecimal totalAmount;
    private LocalDate orderDate;
    private LocalDate endDate;
    private String guestName;
    private String guestPhone;
    private String guestEmail;
    private String remark;
    private Integer status;
    private LocalDateTime payTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}