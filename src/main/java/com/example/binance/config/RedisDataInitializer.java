package com.example.binance.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Redis数据初始化类，在应用启动时清除Redis中的相关数据
 */
@Slf4j
@Component
@Order(1) // 确保在其他初始化器之前运行
public class RedisDataInitializer implements CommandLineRunner {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void run(String... args) {
        log.info("应用启动时清除Redis数据...");
        try {
            // 清除liquidation:*相关的数据
            // 获取所有匹配的键
            redisTemplate.keys("liquidation:*").forEach(key -> {
                log.debug("删除Redis键: {}", key);
                redisTemplate.delete(key);
            });
            
            // 清除active_symbols数据
            if (redisTemplate.hasKey("active_symbols")) {
                log.debug("删除Redis键: active_symbols");
                redisTemplate.delete("active_symbols");
            }
            
            log.info("Redis数据清除完成");
        } catch (Exception e) {
            log.error("清除Redis数据时出错:", e);
        }
    }
}