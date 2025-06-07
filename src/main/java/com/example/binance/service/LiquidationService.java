package com.example.binance.service;

import com.example.binance.model.LiquidationData;
import com.example.binance.model.LiquidationOrder;
import com.example.binance.model.LiquidationStats;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 清算服务类
 */
@Slf4j
@Service
public class LiquidationService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 处理清算数据并存储到Redis
     */
    public void processLiquidationData(Map<String, Object> data) {
        if (!data.containsKey("o")) return;

        try {
            Map<String, Object> orderMap = (Map<String, Object>) data.get("o");
            LiquidationOrder order = objectMapper.convertValue(orderMap, LiquidationOrder.class);
            String symbol = order.getS();
            
            // 计算清算价值
            double value = Double.parseDouble(order.getP()) * Double.parseDouble(order.getQ());
            order.setValue(value);

            // 获取当前时间戳（毫秒）
            long currentTime = System.currentTimeMillis();
            // 15分钟前的时间戳
            long fifteenMinutesAgo = currentTime - 15 * 60 * 1000;
            
            // 从Redis获取现有数据
            LiquidationData liquidationData = new LiquidationData();
            Object existingData = redisTemplate.opsForValue().get("liquidation:" + symbol);
            
            if (existingData != null) {
                liquidationData = objectMapper.convertValue(existingData, LiquidationData.class);
            }

            // 添加订单到订单列表
            liquidationData.getOrders().add(order);

            // 过滤掉15分钟前的订单
            List<LiquidationOrder> filteredOrders = liquidationData.getOrders().stream()
                    .filter(o -> o.getT() > fifteenMinutesAgo)
                    .collect(Collectors.toList());
            liquidationData.setOrders(filteredOrders);

            // 将更新后的数据存储回Redis
            redisTemplate.opsForValue().set("liquidation:" + symbol, liquidationData);

            // 获取当前活跃交易对列表
            Object activeSymbolsObj = redisTemplate.opsForValue().get("active_symbols");
            
            List<String> activeSymbols = activeSymbolsObj != null ?
                    objectMapper.convertValue(activeSymbolsObj, List.class) : new ArrayList<>();
            
            // 如果当前交易对不在列表中，添加它
            if (!activeSymbols.contains(symbol)) {
                activeSymbols.add(symbol);
                redisTemplate.opsForValue().set("active_symbols", activeSymbols);
            }

        } catch (Exception e) {
            log.error("处理清算数据时出错:", e);
        }
    }

    /**
     * 定期清理过期数据（每15分钟运行一次）
     */
    @Scheduled(fixedRate = 900000)
    public void cleanupExpiredData() {
        log.info("开始执行清理过期数据任务 - 执行时间: {}", new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
        try {
            // 获取当前时间戳（毫秒）
            long currentTime = System.currentTimeMillis();
            // 15分钟前的时间戳
            long fifteenMinutesAgo = currentTime - 15 * 60 * 1000;

            // 获取所有活跃交易对
            Object activeSymbolsObj = redisTemplate.opsForValue().get("active_symbols");
            
            List<String> activeSymbols = activeSymbolsObj != null ?
                    objectMapper.convertValue(activeSymbolsObj, List.class) : new ArrayList<>();

            // 创建一个列表来存储需要移除的交易对
            List<String> symbolsToRemove = new ArrayList<>();

            for (String symbol : activeSymbols) {
                // 获取当前交易对的清算数据
                Object dataObj = redisTemplate.opsForValue().get("liquidation:" + symbol);
                
                if (dataObj != null) {
                    LiquidationData liquidationData = objectMapper.convertValue(dataObj, LiquidationData.class);
                    
                    // 过滤掉15分钟前的订单
                    List<LiquidationOrder> filteredOrders = liquidationData.getOrders().stream()
                            .filter(order -> order.getT() > fifteenMinutesAgo)
                            .collect(Collectors.toList());
                    
                    // 如果过滤后没有订单，从活跃交易对列表中移除
                    if (filteredOrders.isEmpty()) {
                        symbolsToRemove.add(symbol);
                        redisTemplate.delete("liquidation:" + symbol);
                    } else {
                        // 更新过滤后的数据
                        liquidationData.setOrders(filteredOrders);
                        redisTemplate.opsForValue().set("liquidation:" + symbol, liquidationData);
                    }
                }
            }
            
            // 从活跃交易对列表中移除需要移除的交易对
            activeSymbols.removeAll(symbolsToRemove);
            
            // 更新活跃交易对列表
            redisTemplate.opsForValue().set("active_symbols", activeSymbols);
            
            log.info("清理过期数据任务完成 - 移除了{}个交易对的过期数据", symbolsToRemove.size());
        } catch (Exception e) {
            log.error("清理过期数据时出错:", e);
        }
    }

    /**
     * 获取特定交易对的清算统计数据
     */
    public LiquidationStats getLiquidationStats(String symbol) {
        LiquidationStats stats = new LiquidationStats();
        
        try {
            Object dataObj = redisTemplate.opsForValue().get("liquidation:" + symbol);
            
            if (dataObj != null) {
                LiquidationData liquidationData = objectMapper.convertValue(dataObj, LiquidationData.class);
                List<LiquidationOrder> orders = liquidationData.getOrders();
                
                // 计算统计数据
                List<LiquidationOrder> longOrders = orders.stream()
                        .filter(order -> "SELL".equals(order.getSide()))
                        .collect(Collectors.toList());
                
                List<LiquidationOrder> shortOrders = orders.stream()
                        .filter(order -> "BUY".equals(order.getSide()))
                        .collect(Collectors.toList());
                
                double totalValue = orders.stream()
                        .mapToDouble(LiquidationOrder::getValue)
                        .sum();
                
                // 找出最大清算量
                LiquidationOrder maxOrder = orders.stream()
                        .max(Comparator.comparing(LiquidationOrder::getValue))
                        .orElse(new LiquidationOrder());
                
                // 找出最新订单
                LiquidationOrder latestOrder = orders.stream()
                        .max(Comparator.comparing(LiquidationOrder::getT))
                        .orElse(new LiquidationOrder());
                
                // 计算平均价格
                double avgPrice = orders.stream()
                        .mapToDouble(order -> {
                            if (order.getAp() != null && !order.getAp().isEmpty()) {
                                return Double.parseDouble(order.getAp());
                            } else {
                                return Double.parseDouble(order.getP());
                            }
                        })
                        .average()
                        .orElse(0);
                
                // 计算振幅
                double latestP = latestOrder.getT() != null ? Double.parseDouble(latestOrder.getP()) : 0;
                double amplitude = avgPrice > 0 ? ((latestP - avgPrice) / avgPrice) * 100 : 0;
                
                // 设置统计数据
                stats.setOrders(orders);
                stats.setLongCount(longOrders.size());
                stats.setShortCount(shortOrders.size());
                stats.setTotalValue(totalValue);
                stats.setLastPrice(latestP);
                stats.setAvgPrice(avgPrice);
                stats.setAmplitude(amplitude);
                stats.setMaxValue(maxOrder.getValue() != null ? maxOrder.getValue() : 0);
                stats.setMaxValueSide(maxOrder.getSide());
                stats.setLastUpdateTime(latestOrder.getT() != null ? latestOrder.getT() : 0);
            }
        } catch (Exception e) {
            log.error("获取清算统计数据时出错:", e);
        }
        
        return stats;
    }

    /**
     * 获取所有交易对的清算统计数据
     */
    public Map<String, LiquidationStats> getAllLiquidationStats() {
        Map<String, LiquidationStats> result = new HashMap<>();
        
        try {
            Object activeSymbolsObj = redisTemplate.opsForValue().get("active_symbols");
            
            List<String> activeSymbols = activeSymbolsObj != null ?
                    objectMapper.convertValue(activeSymbolsObj, List.class) : new ArrayList<>();
            
            for (String symbol : activeSymbols) {
                LiquidationStats stats = getLiquidationStats(symbol);
                result.put(symbol, stats);
            }
        } catch (Exception e) {
            log.error("获取所有清算统计数据时出错:", e);
        }
        
        return result;
    }
    
    /**
     * 获取所有活跃交易对
     */
    public List<String> getActiveSymbols() {
        Object activeSymbolsObj = redisTemplate.opsForValue().get("active_symbols");
        
        return activeSymbolsObj != null ?
                objectMapper.convertValue(activeSymbolsObj, List.class) : new ArrayList<>();
    }
}
