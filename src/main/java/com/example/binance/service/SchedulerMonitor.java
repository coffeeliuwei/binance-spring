package com.example.binance.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 定时任务监控器
 * 用于记录定时任务的执行时间，帮助验证定时任务的执行频率
 */
@Slf4j
@Component
public class SchedulerMonitor {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * 每分钟记录一次当前时间，用于监控定时任务的执行
     */
    @Scheduled(fixedRate = 60000)
    public void logCurrentTime() {
        log.info("定时任务监控 - 当前时间: {}", LocalDateTime.now().format(formatter));
    }
    
    /**
     * 每15分钟执行一次，与cleanupExpiredData方法的执行频率相同
     * 用于验证15分钟定时任务是否按预期执行
     */
    @Scheduled(fixedRate = 900000)
    public void logFifteenMinuteTask() {
        log.info("15分钟定时任务监控 - 执行时间: {}", LocalDateTime.now().format(formatter));
    }
}