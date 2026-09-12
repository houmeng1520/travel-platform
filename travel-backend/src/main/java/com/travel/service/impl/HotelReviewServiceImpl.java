package com.travel.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.entity.HotelReview;
import com.travel.mapper.HotelReviewMapper;
import com.travel.service.HotelReviewService;
import org.springframework.stereotype.Service;

@Service
public class HotelReviewServiceImpl extends ServiceImpl<HotelReviewMapper, HotelReview> implements HotelReviewService {
}