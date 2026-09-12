package com.travel.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.entity.Scenic;
import com.travel.mapper.ScenicMapper;
import com.travel.service.ScenicService;
import org.springframework.stereotype.Service;

@Service  // ← 告诉Spring这是一个Service组件
public class ScenicServiceImpl
        extends ServiceImpl<ScenicMapper, Scenic>  // ← 继承框架提供的实现
        implements ScenicService {                // ← 实现我们定义的接口
    // 不需要写任何方法！框架已经帮我们实现了
}