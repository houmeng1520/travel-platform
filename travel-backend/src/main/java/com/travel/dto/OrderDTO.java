package com.travel.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class OrderDTO {
    @NotBlank(message = "订单类型不能为空")
    private String orderType;  // hotel / scenic / food / route

    @NotNull(message = "目标ID不能为空")
    private Long targetId;

    private Integer quantity = 1;
    private Integer nights = 1;
    private LocalDate orderDate;
    private LocalDate endDate;

    @NotBlank(message = "联系人不能为空")
    private String guestName;

    @NotBlank(message = "联系电话不能为空")
    private String guestPhone;

    private String guestEmail;
    private String remark;
}