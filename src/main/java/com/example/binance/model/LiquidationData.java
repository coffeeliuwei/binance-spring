package com.example.binance.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 清算数据模型，包含订单列表
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LiquidationData {
    private List<LiquidationOrder> orders = new ArrayList<>();
}
