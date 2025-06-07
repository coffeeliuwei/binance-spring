package com.example.binance.controller;

import com.example.binance.model.LiquidationStats;
import com.example.binance.service.LiquidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 清算数据控制器
 */
@RestController
@RequestMapping("/api")
public class LiquidationController {

    @Autowired
    private LiquidationService liquidationService;

    /**
     * 获取所有活跃交易对
     */
    @GetMapping("/symbols")
    public ResponseEntity<List<String>> getActiveSymbols() {
        List<String> symbols = liquidationService.getActiveSymbols();
        return ResponseEntity.ok(symbols);
    }

    /**
     * 获取特定交易对的15分钟数据
     */
    @GetMapping("/liquidation/{symbol}")
    public ResponseEntity<?> getLiquidationData(@PathVariable String symbol) {
        LiquidationStats stats = liquidationService.getLiquidationStats(symbol);
        if (stats.getOrders() == null || stats.getOrders().isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(stats);
    }

    /**
     * 获取所有交易对的15分钟数据
     */
    @GetMapping("/liquidation")
    public ResponseEntity<Map<String, LiquidationStats>> getAllLiquidationData() {
        Map<String, LiquidationStats> result = liquidationService.getAllLiquidationStats();
        return ResponseEntity.ok(result);
    }
}
