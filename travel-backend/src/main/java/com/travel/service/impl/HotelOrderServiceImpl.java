package com.travel.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.entity.HotelOrder;
import com.travel.mapper.HotelOrderMapper;
import com.travel.service.HotelOrderService;
import org.springframework.stereotype.Service;

@Service
public class HotelOrderServiceImpl extends ServiceImpl<HotelOrderMapper, HotelOrder> implements HotelOrderService {
}