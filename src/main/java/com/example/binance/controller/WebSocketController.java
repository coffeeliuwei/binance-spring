package com.example.binance.controller;

import com.example.binance.service.LiquidationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * WebSocket消息处理控制器
 */
@RestController
@RequestMapping("/api")
public class WebSocketController {

    @Autowired
    private LiquidationService liquidationService;
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 处理从前端发送的清算数据
     */
    @PostMapping("/liquidation")
    public ResponseEntity<String> processLiquidationData(@RequestBody Map<String, Object> data) {
        try {
            liquidationService.processLiquidationData(data);
            return ResponseEntity.ok("{\"status\":\"success\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"status\":\"error\",\"message\":\"" + e.getMessage() + "\"}");
        }
    }
}
