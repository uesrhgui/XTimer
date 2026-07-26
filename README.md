# XTimer —— 分布式定时任务调度系统

基于 Java 语言开发的一个微服务系统，可以用做微服务下的闹钟服务系统，支持高精准，高负载的定时需求处理，可以向各个业 务提供定时任务服务。例如帮助设备状态自动巡检系统实现定时巡检任务的注册功能。

## 架构概览

```
┌──────────────────────────────────────────────────┐
│                    junTime-api                     │
│           Feign Client / DTO 接口定义               │
└──────────────────────┬───────────────────────────┘
                       │
┌──────────────────────▼───────────────────────────┐
│                  junTime-xtimer                    │
│  ┌──────────┐ ┌──────────┐ ┌───────────────────┐ │
│  │ Scheduler│ │ Trigger  │ │     Executor      │ │
│  │ (分片调度) │ │ (时间窗拉取)│ │  (HTTP 回调执行)   │ │
│  └──────────┘ └──────────┘ └───────────────────┘ │
│  ┌──────────┐                                    │
│  │ Migrator │  (定时迁移 & 高可用保障)              │
│  └──────────┘                                    │
└──────────────────────┬───────────────────────────┘
                       │
┌──────────────────────▼───────────────────────────┐
│                  junTime-common                    │
│        Redis 分布式锁 / JWT / 通用工具              │
└──────────────────────────────────────────────────┘
```

## 核心模块

| 模块 | 说明 |
|------|------|
| **junTime-api** | Feign 客户端接口与 DTO 定义，供业务方通过 RPC 调用定时任务服务 |
| **junTime-common** | 通用组件：Redis 可重入分布式锁、JWT 工具类、统一响应模型 |
| **junTime-xtimer** | 核心调度服务：调度器、触发器、执行器、迁移器 |
| **junTime-testconsumer** | 测试消费者，演示如何接入 XTimer 服务 |

## 核心设计

### 调度器 (Scheduler)
- 每秒轮询所有时间分片（默认 5 个 Bucket）
- 基于 Redis 分布式锁保证同一分片仅被一个实例处理
- 使用异步线程池并行处理分片任务

### 触发器 (Trigger)
- 通过 Redis ZSET 按时间窗口 `zrange` 拉取到期任务
- 每个 Worker 独立持有当前分钟桶的锁，处理该分钟内所有 Timer
- 可配置 `zrange` 间隔和 Worker 数量

### 执行器 (Executor)
- 通过 `RestTemplate` 向业务方发起 HTTP 回调
- 支持 POST 方法，可配置回调 URL 和 Body
- 记录每次执行的耗时、输出和状态

### 迁移器 (Migrator)
- 每小时扫描所有激活状态的 Timer
- 将 Timer 的 Cron 表达式解析后生成执行时间，写入 Redis ZSET
- 基于分布式锁防止重复迁移

## 技术栈

| 类别 | 技术 |
|------|------|
| 语言 | Java 8 |
| 框架 | Spring Boot 2.7.7 |
| 微服务 | Spring Cloud 2021.0.5 + Alibaba Nacos |
| 数据库 | MySQL 8.0 + MyBatis |
| 缓存/队列 | Redis（Jedis）+ ZSET |
| 连接池 | Druid |
| 工具库 | Lombok、Hutool、Guava |
| 认证 | JWT（jjwt） |

## 快速开始

### 环境要求

- JDK 1.8+
- MySQL 8.0+
- Redis 5.0+
- Nacos 2.2+

### 1. 初始化数据库

执行 `db/` 目录下的 SQL 脚本创建数据库和表：

```bash
mysql -u root -p < db/bitstorm-svr-xtimer.sql
```

### 2. 配置

编辑 `junTime-xtimer/src/main/resources/application.yml`，修改数据库和 Redis 连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/xtimer?...
    username: root
    password: ${MYSQL_PASSWORD}  # 通过环境变量设置
  redis:
    host: localhost
    port: 6379
```

### 3. 启动 Nacos

```bash
# 单机模式启动
sh startup.sh -m standalone
```

### 4. 启动服务

```bash
# 编译
mvn clean package -DskipTests

# 启动 XTimer
java -jar junTime-xtimer/target/junTime-xtimer.jar
```

## API 接口

### 创建定时任务

```json
POST /xtimer/create
{
  "app": "your-app-name",
  "name": "任务名称",
  "cron": "0 * * ? * *",
  "notifyHTTPParam": {
    "url": "http://your-service/callback",
    "method": "POST",
    "body": "{\"msg\":\"定时触发\"}"
  }
}
```

### 启用/关闭定时任务

```json
POST /xtimer/enable?app=your-app&timerId=1
POST /xtimer/unable?app=your-app&timerId=1
```

### Feign Client 调用

```java
@Resource
private XTimerClient xTimerClient;

// 创建 Timer
Long timerId = xTimerClient.createTimer(timerDTO);

// 启用 Timer
xTimerClient.enableTimer("myApp", timerId);
```

## 项目结构

```
XTimer
├── junTime-api              # Feign 接口定义 & DTO
│   └── src/main/java/cn/bitoffer/api
├── junTime-common           # 通用组件（Redis锁、JWT、工具类）
│   └── src/main/java/cn/bitoffer/common
├── junTime-xtimer           # 核心调度服务
│   └── src/main/java/cn/bitoffer/xtimer
│       ├── controller       # Web 接口
│       ├── service
│       │   ├── impl         # 业务逻辑
│       │   ├── scheduler    # 调度器
│       │   ├── trigger      # 触发器
│       │   ├── executor     # 执行器
│       │   └── migretor     # 迁移器
│       ├── mapper           # MyBatis Mapper
│       ├── model            # 数据模型
│       ├── enums            # 枚举定义
│       ├── redis            # Redis 缓存
│       └── common           # 配置 & 线程池
├── junTime-testconsumer     # 测试消费者
├── db                       # 数据库初始化脚本
└── wrkbench                 # 压测脚本
```

## 配置参数

| 参数 | 默认值 | 说明 |
|------|--------|------|
| `scheduler.bucketsNum` | 5 | 时间分片桶数量 |
| `scheduler.tryLockSeconds` | 70 | 调度锁持有时间（秒） |
| `trigger.zrangeGapSeconds` | 1 | 时间窗口内 ZSET 拉取间隔 |
| `trigger.workersNum` | 10000 | Trigger Worker 数量 |
| `migrator.workersNum` | 1000 | 迁移 Worker 数量 |
| `migrator.migrateStepMinutes` | 60 | 每次迁移覆盖时间步长（分钟） |

