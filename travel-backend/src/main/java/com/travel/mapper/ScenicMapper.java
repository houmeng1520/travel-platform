package com.travel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.travel.entity.Scenic;
import org.apache.ibatis.annotations.Mapper;

@Mapper  // ← 告诉Spring这是一个Mapper
public interface ScenicMapper extends BaseMapper<Scenic> {
    // 不需要写任何方法！
    // BaseMapper提供了：insert、deleteById、updateById、selectById、selectList等
}