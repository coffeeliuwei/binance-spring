package com.example.binance.config;

import com.example.binance.service.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.stereotype.Component;

/**
 * WebSocket初始化类，在应用启动时初始化WebSocket连接
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "spring.websocket.enabled", havingValue = "true", matchIfMissing = false)
public class WebSocketInitializer implements CommandLineRunner {

    @Autowired
    private WebSocketService webSocketService;

    @Override
    public void run(String... args) {
        log.info("初始化币安WebSocket连接...");
        webSocketService.initBinanceWebSocket();
    }
}
