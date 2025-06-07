package com.example.binance.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

/**
 * 清算统计数据模型
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LiquidationStats {
    // 订单列表
    private List<LiquidationOrder> orders;
    // 多单数量
    private int longCount;
    // 空单数量
    private int shortCount;
    // 总价值
    private double totalValue;
    // 最新价格
    private double lastPrice;
    // 平均价格
    private double avgPrice;
    // 振幅
    private double amplitude;
    // 最大价值
    private double maxValue;
    // 最大价值方向
    private String maxValueSide;
    // 最后更新时间
    private long lastUpdateTime;
}
