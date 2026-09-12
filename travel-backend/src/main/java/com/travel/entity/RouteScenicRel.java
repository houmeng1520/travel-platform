package com.travel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("route_scenic_rel")
public class RouteScenicRel {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long routeId;
    private Long scenicId;
    private Integer dayIndex;
    private Integer sortOrder;
    private String stayTime;
}