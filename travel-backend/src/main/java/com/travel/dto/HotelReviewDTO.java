package com.travel.dto;

import lombok.Data;

@Data
public class HotelReviewDTO {
    private Long orderId;
    private Long hotelId;
    private Integer rating;
    private String content;
    private String images;
}