# 币安强平订单监控系统

## 项目简介

这是一个基于Spring Boot的币安强平订单监控系统，用于实时监控和分析币安期货市场的强平（清算）订单数据。系统通过WebSocket连接到币安API，获取实时的强平订单数据，并使用Redis进行数据存储和分析。

## 功能特点

- 实时监控币安期货市场的强平订单
- 提供15分钟内强平订单的统计分析
- 支持按交易对过滤数据
- 可视化展示强平订单的方向、价格、数量和价值
- 计算并展示多空比例、总价值、最大单等统计数据

## 技术栈

- Spring Boot 3.2.3
- Spring WebSocket
- Spring Data Redis
- Thymeleaf
- HTML/CSS/JavaScript

## 系统要求

- Java 17+
- Redis 6.0+
- Maven 3.6+

## 安装和运行

### 1. 克隆项目

```bash
git clone https://github.com/yourusername/binance-liquidation-monitor.git
cd binance-liquidation-monitor
```

### 2. 配置Redis

确保Redis服务器已启动并运行在默认端口(6379)。如需修改Redis配置，请编辑`src/main/resources/application.properties`文件。

### 3. 构建和运行项目

```bash
mvn clean package
java -jar target/binance-0.0.1-SNAPSHOT.jar
```

或者使用Maven Spring Boot插件直接运行：

```bash
mvn spring-boot:run
```

### 4. 访问应用

打开浏览器，访问：http://localhost:3000

## 使用说明

### 实时数据页面

- 显示实时的强平订单数据
- 可以通过输入框过滤特定交易对
- 点击"打开行情窗口"可以查看行情数据

### 15分钟统计页面

- 显示过去15分钟内的强平订单统计数据
- 按总价值降序排列
- 显示多空比例、总价值、最大单等统计信息

## 项目结构

```
src/main/java/com/example/binance/
├── BinanceApplication.java        # 应用程序入口
├── config/                        # 配置类
│   ├── RedisConfig.java           # Redis配置
│   ├── WebSocketConfig.java       # WebSocket配置
│   └── WebSocketInitializer.java  # WebSocket初始化
├── controller/                    # 控制器
│   ├── LiquidationController.java # 清算数据API控制器
│   ├── PageController.java        # 页面控制器
│   └── WebSocketController.java   # WebSocket消息处理控制器
├── model/                         # 数据模型
│   ├── LiquidationData.java       # 清算数据模型
│   ├── LiquidationOrder.java      # 清算订单模型
│   └── LiquidationStats.java      # 清算统计数据模型
└── service/                       # 服务
    ├── LiquidationService.java    # 清算服务
    └── WebSocketService.java      # WebSocket服务
```

## 注意事项

- 本项目仅用于学习和研究目的
- 交易决策不应仅基于本系统提供的数据
- 币安API可能会有访问限制，请遵循币安的API使用政策

## 许可证

[MIT License](LICENSE)
