package com.travel.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class HotelOrderDTO {
    @NotNull(message = "酒店ID不能为空")
    private Long hotelId;

    private String roomType;

    @NotNull(message = "入住日期不能为空")
    private LocalDate checkInDate;

    @NotNull(message = "离店日期不能为空")
    private LocalDate checkOutDate;

    private Integer quantity = 1;

    @NotBlank(message = "入住人姓名不能为空")
    private String guestName;

    @NotBlank(message = "入住人电话不能为空")
    private String guestPhone;

    private String guestEmail;
    private String remark;
}