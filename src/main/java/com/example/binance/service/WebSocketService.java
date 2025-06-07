package com.example.binance.service;

import com.example.binance.model.LiquidationOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket服务类
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "spring.websocket.enabled", havingValue = "true", matchIfMissing = false)
@ServerEndpoint("/ws")
public class WebSocketService {

    private static LiquidationService liquidationService;
    
    private static final Map<Session, WebSocketService> clients = new ConcurrentHashMap<>();
    private Session session;
    private static Session binanceSession;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    @Value("${spring.autoconfigure.exclude:}")
    private String autoConfigExclude;
    
    @Autowired
    public void setLiquidationService(LiquidationService service) {
        WebSocketService.liquidationService = service;
    }
    
    /**
     * 处理清算数据
     */
    public void processLiquidationData(Map<String, Object> data) {
        try {
            if (data.containsKey("o")) {
                liquidationService.processLiquidationData(data);
            }
        } catch (Exception e) {
            log.error("处理清算数据时出错:", e);
        }
    }
    
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        clients.put(session, this);
        log.info("客户端已连接: {}", session.getId());
    }
    
    @OnClose
    public void onClose() {
        clients.remove(session);
        log.info("客户端连接已关闭: {}", session.getId());
    }
    
    @OnMessage
    public void onMessage(String message, Session session) {
        try {
            Map<String, Object> data = objectMapper.readValue(message, Map.class);
            if (data.containsKey("o")) {
                // 处理清算数据
                liquidationService.processLiquidationData(data);
            }
        } catch (Exception e) {
            log.error("处理客户端消息时出错:", e);
        }
    }
    
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("WebSocket错误:", error);
    }
    
    /**
     * 初始化币安WebSocket连接
     */
    public void initBinanceWebSocket() {
        // 如果Redis自动配置被排除，则不初始化WebSocket连接
        if (autoConfigExclude != null && autoConfigExclude.contains("RedisAutoConfiguration")) {
            log.info("Redis自动配置已禁用，跳过币安WebSocket连接初始化");
            return;
        }
        
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            String uri = "wss://fstream.binance.com/ws/!forceOrder@arr";
            binanceSession = container.connectToServer(new Endpoint() {
                @Override
                public void onOpen(Session session, EndpointConfig config) {
                    log.info("已连接到币安WebSocket API");
                    
                    session.addMessageHandler(new MessageHandler.Whole<String>() {
                        @Override
                        public void onMessage(String message) {
                            try {
                                Map<String, Object> data = objectMapper.readValue(message, Map.class);
                                if (data.containsKey("o")) {
                                    // 处理清算数据
                                    liquidationService.processLiquidationData(data);
                                }
                            } catch (Exception e) {
                                log.error("处理WebSocket消息时出错:", e);
                            }
                        }
                    });
                }
            }, ClientEndpointConfig.Builder.create().build(), URI.create(uri));
            
            // 设置关闭处理器
            binanceSession.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    // 处理关闭消息
                }
            });
            
            log.info("币安WebSocket连接已初始化");
        } catch (Exception e) {
            log.error("初始化币安WebSocket连接时出错:", e);
        }
    }
}
