# 币安强平订单监控系统

## 项目简介

这是一个基于Spring Boot的币安强平订单监控系统，用于实时监控和分析币安期货市场的强平（清算）订单数据。系统通过WebSocket连接到币安API，获取实时的强平订单数据，并使用Redis进行数据存储和分析。

## 功能特点

### 核心功能
- 🔄 **实时数据监控**：通过WebSocket连接币安API，实时获取强平订单数据
- 📊 **数据统计分析**：提供15分钟内强平订单的详细统计分析
- 🎯 **交易对过滤**：支持按特定交易对过滤和查看数据
- 📈 **可视化展示**：使用D3.js热力图展示强平订单的方向、价格、数量和价值
- 🧮 **智能计算**：自动计算多空比例、总价值、平均价格、振幅、最大单等统计数据
- ⚡ **高性能缓存**：使用Redis缓存数据，支持高并发访问
- 🔧 **自动清理**：定时清理过期数据，保持系统性能

### 页面功能
- **实时监控页面**：展示实时强平订单流，支持交易对筛选
- **15分钟统计页面**：展示各交易对的统计数据，按总价值排序
- **热力图可视化**：直观展示价格变化和交易活跃度

## 技术栈

### 后端技术
- **Spring Boot 3.2.3** - 主框架
- **Spring WebSocket** - WebSocket支持
- **Spring Data Redis** - Redis数据访问
- **Spring Web** - RESTful API
- **Thymeleaf** - 模板引擎
- **Jackson** - JSON序列化
- **Lombok** - 代码简化

### 前端技术
- **HTML5/CSS3** - 页面结构和样式
- **JavaScript (ES6+)** - 交互逻辑
- **D3.js** - 数据可视化
- **WebSocket API** - 实时通信

### 数据存储
- **Redis** - 内存数据库，用于缓存和实时数据存储

## 系统要求

- **Java 17+** - 运行环境
- **Redis 6.0+** - 数据存储
- **Maven 3.6+** - 构建工具
- **现代浏览器** - 支持WebSocket和ES6+

## 安装和运行

### 1. 克隆项目

```bash
git clone <your-repository-url>
cd binance-spring
```

### 2. 环境准备

#### 安装Redis
- **Windows**: 下载并安装Redis for Windows
- **macOS**: `brew install redis`
- **Linux**: `sudo apt-get install redis-server`

启动Redis服务：
```bash
# Windows
redis-server

# macOS/Linux
redis-server /usr/local/etc/redis.conf
```

#### 验证Redis连接
```bash
redis-cli ping
# 应该返回: PONG
```

### 3. 配置文件

项目配置文件位于 `src/main/resources/application.properties`：

```properties
# 服务器配置
server.port=3000

# Redis配置
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.data.redis.enabled=true

# 日志配置
logging.level.root=INFO
logging.level.com.example.binance=DEBUG

# WebSocket配置（可选）
spring.websocket.enabled=false
```

### 4. 构建和运行

#### 方式一：使用Maven直接运行
```bash
mvn spring-boot:run
```

#### 方式二：打包后运行
```bash
mvn clean package
java -jar target/binance-0.0.1-SNAPSHOT.jar
```

### 5. 访问应用

启动成功后，打开浏览器访问：
- **主页**: http://localhost:3000
- **实时监控**: http://localhost:3000/
- **15分钟统计**: http://localhost:3000/15min
- **行情窗口**: http://localhost:3000/market

## 使用说明

### 页面功能详解

#### 1. 实时监控页面 (`/`)
- 📊 **实时数据流**：显示最新的强平订单数据
- 🔍 **交易对过滤**：通过输入框筛选特定交易对（如：BTCUSDT）
- 📈 **热力图展示**：使用D3.js可视化展示价格变化
- 🎯 **行情窗口**：点击"打开行情窗口"查看详细行情数据
- ⚡ **实时更新**：数据自动刷新，无需手动操作

#### 2. 15分钟统计页面 (`/15min`)
- 📋 **统计汇总**：展示过去15分钟内各交易对的统计数据
- 📊 **排序显示**：按总价值降序排列，突出重要交易对
- 🧮 **详细指标**：
  - 多空比例（Long/Short Ratio）
  - 总价值（Total Value）
  - 平均价格（Average Price）
  - 价格振幅（Amplitude）
  - 最大单笔价值（Max Value）
- 🎨 **颜色编码**：根据多空比例显示不同颜色
- 🔗 **快速跳转**：点击交易对名称跳转到详情页面

#### 3. 行情窗口 (`/market`)
- 📈 **价格展示**：显示实时价格信息
- 📊 **图表分析**：提供价格走势图表
- 🔄 **自动刷新**：定时更新最新行情数据

### 操作指南

1. **启动应用**：确保Redis运行后启动Spring Boot应用
2. **访问页面**：浏览器打开 http://localhost:3000
3. **查看实时数据**：主页自动显示实时强平订单
4. **过滤数据**：在搜索框输入交易对名称进行过滤
5. **查看统计**：点击"15分钟统计"查看汇总数据
6. **分析趋势**：通过热力图和统计数据分析市场趋势

## API接口文档

### RESTful API

#### 1. 获取活跃交易对
```http
GET /api/symbols
```
**响应示例**：
```json
["BTCUSDT", "ETHUSDT", "ADAUSDT"]
```

#### 2. 获取特定交易对数据
```http
GET /api/liquidation/{symbol}
```
**参数**：
- `symbol`: 交易对名称（如：BTCUSDT）

**响应示例**：
```json
{
  "orders": [...],
  "longCount": 15,
  "shortCount": 8,
  "totalValue": 1250000.50,
  "lastPrice": 43250.00,
  "avgPrice": 43180.25,
  "amplitude": 2.5,
  "maxValue": 85000.00
}
```

#### 3. 获取所有交易对数据
```http
GET /api/liquidation
```
**响应示例**：
```json
{
  "BTCUSDT": { /* 统计数据 */ },
  "ETHUSDT": { /* 统计数据 */ }
}
```

#### 4. 提交清算数据
```http
POST /api/liquidation
Content-Type: application/json
```
**请求体**：币安WebSocket消息格式

### 数据模型

#### LiquidationOrder（清算订单）
```json
{
  "s": "BTCUSDT",      // 交易对
  "S": "SELL",         // 方向（SELL=多单强平，BUY=空单强平）
  "o": "LIMIT",        // 订单类型
  "q": "0.001",        // 数量
  "p": "43250.00",     // 价格
  "ap": "43250.00",    // 平均价格
  "X": "FILLED",       // 执行状态
  "l": "0.001",        // 最后成交数量
  "z": "0.001",        // 累计成交数量
  "T": 1640995200000,   // 成交时间
  "value": 43.25       // 计算得出的价值
}
```

#### LiquidationStats（统计数据）
```json
{
  "orders": [],         // 订单列表
  "longCount": 0,       // 多单数量
  "shortCount": 0,      // 空单数量
  "totalValue": 0.0,    // 总价值
  "lastPrice": 0.0,     // 最新价格
  "avgPrice": 0.0,      // 平均价格
  "amplitude": 0.0,     // 振幅
  "maxValue": 0.0       // 最大单笔价值
}
```

## 项目结构

```
binance-spring/
├── src/
│   ├── main/
│   │   ├── java/com/example/binance/
│   │   │   ├── BinanceApplication.java        # 🚀 应用程序入口
│   │   │   ├── config/                        # ⚙️ 配置层
│   │   │   │   ├── RedisConfig.java           # Redis连接和序列化配置
│   │   │   │   ├── RedisDataInitializer.java  # 启动时数据初始化
│   │   │   │   ├── WebSocketConfig.java       # WebSocket配置
│   │   │   │   └── WebSocketInitializer.java  # WebSocket初始化器
│   │   │   ├── controller/                    # 🎮 控制器层
│   │   │   │   ├── LiquidationController.java # RESTful API控制器
│   │   │   │   ├── PageController.java        # 页面路由控制器
│   │   │   │   └── WebSocketController.java   # WebSocket消息处理
│   │   │   ├── model/                         # 📊 数据模型层
│   │   │   │   ├── LiquidationData.java       # 清算数据容器模型
│   │   │   │   ├── LiquidationOrder.java      # 清算订单实体模型
│   │   │   │   └── LiquidationStats.java      # 统计数据模型
│   │   │   └── service/                       # 🔧 服务层
│   │   │       ├── LiquidationService.java    # 核心业务逻辑服务
│   │   │       └── WebSocketService.java      # WebSocket连接服务
│   │   └── resources/
│   │       ├── application.properties         # 📝 应用配置文件
│   │       ├── static/                        # 🎨 静态资源
│   │       │   ├── css/                       # 样式文件
│   │       │   ├── js/                        # JavaScript文件
│   │       │   └── images/                    # 图片资源
│   │       └── templates/                     # 📄 Thymeleaf模板
│   │           ├── index.html                 # 实时监控页面
│   │           ├── 15min.html                 # 15分钟统计页面
│   │           └── market.html                # 行情窗口页面
│   └── test/                                  # 🧪 测试代码
│       └── java/
├── target/                                    # 📦 构建输出目录
├── pom.xml                                    # 📋 Maven项目配置
├── README.md                                  # 📖 项目说明文档
├── 实训项目指导书.md                           # 📚 详细技术指导
├── 实训任务书.md                              # 📋 实训任务说明
└── 实训报告要求.md                            # 📝 报告编写要求
```

### 核心模块说明

#### 🏗️ 架构设计
- **分层架构**：Controller → Service → Model，职责清晰
- **配置分离**：统一的配置管理，便于环境切换
- **服务解耦**：业务逻辑与数据访问分离

#### 📊 数据流向
1. **数据接收**：WebSocket接收币安API数据
2. **数据处理**：LiquidationService处理和计算统计
3. **数据存储**：Redis缓存实时数据和统计结果
4. **数据展示**：Controller提供API，前端页面展示

#### 🔧 关键组件
- **RedisConfig**：配置Redis连接和序列化策略
- **LiquidationService**：核心业务逻辑，包含数据处理和统计计算
- **WebSocketService**：管理与币安API的WebSocket连接
- **前端页面**：使用D3.js实现数据可视化

## 注意事项

### ⚠️ 重要提醒
- **学习目的**：本项目仅用于学习和研究目的，不构成投资建议
- **数据准确性**：交易决策不应仅基于本系统提供的数据
- **API限制**：币安API可能会有访问限制，请遵循币安的API使用政策
- **风险提示**：数字货币交易存在高风险，请谨慎投资

### 🔧 技术注意事项
- **Redis依赖**：确保Redis服务正常运行，否则应用无法启动
- **内存使用**：系统会缓存15分钟内的数据，注意内存使用情况
- **网络连接**：需要稳定的网络连接以保持与币安API的WebSocket连接
- **端口占用**：默认使用3000端口，如有冲突请修改配置文件

### 🚀 性能优化建议
- **Redis配置**：根据数据量调整Redis内存配置
- **定时清理**：系统自动清理过期数据，无需手动干预
- **并发处理**：支持多用户同时访问，但建议控制并发数

## 常见问题

### Q: 启动时提示Redis连接失败？
A: 请确保Redis服务已启动，并检查配置文件中的Redis连接信息。

### Q: 页面显示无数据？
A: 检查WebSocket连接是否正常，或者等待币安API推送数据。

### Q: 如何修改端口号？
A: 在`application.properties`文件中修改`server.port`配置。

### Q: 如何启用WebSocket连接？
A: 将`application.properties`中的`spring.websocket.enabled`设置为`true`。

## 贡献指南

欢迎提交Issue和Pull Request来改进项目：

1. Fork本项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启Pull Request

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 联系方式

如有问题或建议，请通过以下方式联系：

- 📧 Email: [coffee.liu@gmail.com]
- 🐛 Issues: [项目Issues页面]
- 📖 文档: 查看项目中的详细指导书

---

⭐ 如果这个项目对您有帮助，请给个Star支持一下！
