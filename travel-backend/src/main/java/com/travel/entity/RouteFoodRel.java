package com.travel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("route_food_rel")
public class RouteFoodRel {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long routeId;
    private Long foodId;
    private Integer dayIndex;
    private String mealType;
    private String recommendation;
}