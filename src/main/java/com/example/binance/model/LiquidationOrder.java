package com.example.binance.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 清算订单模型
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LiquidationOrder {
    // 交易对
    @JsonProperty("s")
    private String s;
    // 订单方向 (BUY/SELL)
    @JsonProperty("S")
    private String side;
    // 价格
    @JsonProperty("p")
    private String p;
    // 数量
    @JsonProperty("q")
    private String q;
    // 时间戳
    @JsonProperty("T")
    private Long T;
    // 平均价格
    @JsonProperty("ap")
    private String ap;
    // 计算得到的价值
    private Double value;
}
