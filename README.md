# Dynamic Project Spring Boot Starter

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Java](https://img.shields.io/badge/Java-25-orange.svg)](https://www.oracle.com/java/) 
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.3-green.svg)](https://spring.io/projects/spring-boot)
[![Groovy](https://img.shields.io/badge/Groovy-5.0.4-purple.svg)](https://groovy-lang.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)

Dynamic Project Spring Boot Starter 是一个企业级动态化解决方案，包含两个核心模块：

- **Dynamic Bean Starter**: 允许在运行时动态创建、更新和删除 Spring Bean，通过 Groovy 脚本定义 Bean 行为，并将脚本存储在数据库中
- **Dynamic Schedule Starter**: 提供基于 Cron 表达式的动态定时任务管理，支持运行时添加、修改和删除定时任务

整个解决方案实现了无需重启应用即可修改业务逻辑和定时任务的功能，极大提升了系统的灵活性和可维护性。

## 🌟 核心特性

### Dynamic Bean Starter
- **动态 Bean 管理**: 在运行时动态注册、更新和删除 Spring Bean
- **Groovy 脚本支持**: 使用 Groovy 脚本定义 Bean 的行为，支持多种函数式接口
- **数据库存储**: 将脚本存储在数据库中，支持持久化和版本管理
- **自定义作用域**: 实现了 `REFRESHABLE_SCOPE` 作用域来管理动态 Bean 生命周期
- **事件驱动**: 基于 Spring 事件机制实现 Bean 的刷新和生命周期管理
- **SAM 接口**: 提供 Single Abstract Method 接口统一支持 `Runnable`, `Consumer`, `Supplier`, `Function`, `Predicate` 等函数式接口
- **IOC 容器访问**: 在 Groovy 脚本中可以直接访问 Spring IOC 容器 (`ioc` 变量)
- **线程局部变量**: 支持通过 `locals` 变量传递线程局部数据

### Dynamic Schedule Starter
- **动态定时任务**: 运行时动态添加、修改、删除定时任务
- **Cron 表达式支持**: 完整支持标准 Cron 表达式语法
- **任务绑定**: 将定时任务与 Dynamic Bean 绑定执行
- **任务管理**: 提供完整的任务注册、注销、重新注册功能
- **线程池管理**: 内置高性能线程池调度器
- **数据库持久化**: 定时任务配置存储在数据库中

## 📚 技术栈

- **Java**: 25
- **Spring Boot**: 4.0.3
- **Groovy**: 5.0.4
- **Liquor Eval JSR223**: 1.5.1 (Groovy 脚本执行引擎)
- **MySQL**: 8.0+ (用于存储脚本和定时任务配置)
- **JDBC**: 数据库访问
- **Lombok**: 简化代码

## 🏗️ 项目结构

```
dynamic-project-spring-boot-parent/
├── dynamic-bean-spring-boot-starter/     # Dynamic Bean 核心starter模块
│   ├── src/main/java/io/github/thetaoofcoding/dynamicbean/
│   │   ├── autoconfigure/                # 自动配置类
│   │   │   ├── DynamicBeanAutoConfiguration.java
│   │   │   └── EarlySourceRegistrar.java
│   │   ├── model/                        # 数据模型
│   │   │   └── RefreshableBeanModel.java
│   │   ├── repository/                   # 数据访问层
│   │   │   └── RefreshableBeanRepository.java
│   │   ├── service/                      # 业务逻辑层
│   │   │   ├── RefreshableBeanService.java
│   │   │   └── impl/RefreshableBeanServiceImpl.java
│   │   ├── util/                         # 工具类
│   │   │   └── Assert.java
│   │   ├── groovy/                       # Groovy 相关组件
│   │   │   ├── GroovyShellFactory.java   # Groovy Shell 工厂
│   │   │   ├── GroovyVariables.java      # Groovy 变量定义
│   │   │   └── SourceResolver.java         # 源解析器
│   │   ├── event/                        # 事件相关
│   │   │   ├── RefreshBeanEvent.java     # 刷新事件
│   │   │   └── RefreshBeanEventListener.java # 事件监听器
│   │   ├── scope/                        # 自定义作用域
│   │   │   └── RefreshableScope.java       # REFRESHABLE_SCOPE 实现
│   │   └── core/                         # 核心接口
│   │       └── SAM.java                    # 单一抽象方法接口
│   └── src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
├── dynamic-schedule-spring-boot-starter/ # Dynamic Schedule starter模块
│   ├── src/main/java/io/github/thetaoofcoding/dynamicschedule/
│   │   ├── autoconfigure/                # 自动配置类
│   │   │   ├── DynamicScheduleAutoConfiguration.java
│   │   │   └── DynamicScheduleInitializer.java
│   │   ├── model/                        # 数据模型
│   │   │   └── ScheduledTaskDefinition.java
│   │   ├── repository/                   # 数据访问层
│   │   │   └── ScheduledTaskDefinitionRepository.java
│   │   ├── service/                      # 业务逻辑层
│   │   │   ├── ScheduledTaskDefinitionService.java
│   │   │   ├── ScheduledTaskService.java
│   │   │   └── impl/
│   │   │       ├── ScheduledTaskDefinitionServiceImpl.java
│   │   │       └── ScheduledTaskRegistrar.java
│   └── src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
├── dynamic-project-spring-boot-sample/  # 示例应用
│   ├── src/main/java/io/github/thetaoofcoding/sample/
│   │   ├── controller/                   # REST控制器
│   │   │   ├── DynamicBeanController.java
│   │   │   └── DynamicScheduleController.java
│   │   └── DynamicProjectSampleApplication.java
│   └── src/main/resources/application.yaml
├── schema/                               # 数据库脚本
│   ├── refreshable_bean.sql              # Dynamic Bean 表结构
│   └── scheduled_task_definition.sql     # Dynamic Schedule 表结构
└── pom.xml                               # 父POM
```

## ⚡ 快速开始

### 1. 数据库设置

首先，执行数据库脚本创建所需的表：

```bash
# 创建 Dynamic Bean 表
mysql -u your_username -p your_database < schema/refreshable_bean.sql

# 创建 Dynamic Schedule 表
mysql -u your_username -p your_database < schema/scheduled_task_definition.sql
```

### 2. 添加依赖

在你的 Spring Boot 项目中添加以下依赖：

```xml
<!-- 仅提供动态bean功能 -->
<dependency>
    <groupId>io.github.thetaoofcoding</groupId>
    <artifactId>dynamic-bean-spring-boot-starter</artifactId>
    <version>1.0</version>
</dependency>
<!-- 提供动态bean以及动态定时任务功能，dynamic-schedule-spring-boot-starter 包含 dynamic-bean-spring-boot-starter -->
<dependency>
    <groupId>io.github.thetaoofcoding</groupId>
    <artifactId>dynamic-schedule-spring-boot-starter</artifactId>
    <version>1.0</version>
</dependency>
```

### 3. 配置数据库

在 `application.yaml` 中配置数据源：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/dynamic_project?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=UTC
    username: your_username
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver
```

### 4. 使用示例
#### REST API 仅为 sample 演示模块提供的，在使用 dynamic-bean-spring-boot-starter 或 dynamic-schedule-spring-boot-starter 时，需自行调用Service层实现并暴露接口

##### Dynamic Bean REST API
| 方法 | 路径 | 描述 |
|------|------|------|
| POST | `/dynamicBean` | 创建动态 Bean |
| PUT | `/dynamicBean` | 更新动态 Bean |
| DELETE | `/dynamicBean/{beanName}` | 删除动态 Bean |
| GET | `/dynamicBean` | 查询所有动态 Bean |
| GET | `/dynamicBean/execute/{beanName}` | 执行指定的动态 Bean |
| GET | `/dynamicBean/beanNames` | 获取内存中所有已注册的动态 Bean 名称 |

##### Dynamic Schedule REST API

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | `/dynamicSchedule` | 查询所有动态定时任务 |
| POST | `/dynamicSchedule` | 创建动态定时任务 |
| PUT | `/dynamicSchedule` | 更新动态定时任务 |
| DELETE | `/dynamicSchedule/{registryKey}` | 删除动态定时任务 |

### 5. 脚本示例

#### Dynamic Bean 脚本示例

```sql
-- 任务型接口示例
INSERT INTO `refreshable_bean` VALUES (1, 'runnable-task', 'return { param -> println "Runnable running ..." } as SAM', '任务型接口示例');

-- 消费型接口示例
INSERT INTO `refreshable_bean` VALUES (2, 'consumer-task', 'return { param -> println "Hello, $param" } as SAM', '消费型接口示例');

-- 供给型接口示例
INSERT INTO `refreshable_bean` VALUES (3, 'supplier-task', 'return { param -> "TheTaoOfCoding"} as SAM', '供给型接口示例');

-- 函数型接口示例
INSERT INTO `refreshable_bean` VALUES (4, 'function-task', 'return { str -> str.length() } as SAM', '函数型接口示例');

-- 断言型接口示例
INSERT INTO `refreshable_bean` VALUES (5, 'predicate-task', 'return { name -> name == "TheTaoOfCoding" } as SAM', '断言型接口示例');

-- 使用内置对象 ioc 查找依赖示例
INSERT INTO `refreshable_bean` VALUES (6, 'run-4-ioc', 'import javax.sql.DataSource; return { param -> println ioc.getBean(DataSource.class) } as SAM', '使用内置对象 ioc 查找依赖示例');

-- 使用内置对象 locals 传递线程变量示例
INSERT INTO `refreshable_bean` VALUES (7, 'run-4-locals', 'return { param -> println "locals.get() = ${locals.get()}, in groovy."; locals.remove(); } as SAM', '使用内置对象 locals 传递线程变量示例');
```

#### Dynamic Schedule 脚本示例

```sql
-- 动态定时任务：统计报表
INSERT INTO `scheduled_task_definition` VALUES (1, 'report-task', '0/5 * * * * ? ', 'dynamic-schedule-1', '动态定时任务：统计报表');

-- 动态定时任务：数据清理
INSERT INTO `scheduled_task_definition` VALUES (2, 'cleanup-task', '0/10 * * * * ? ', 'dynamic-schedule-2', '动态定时任务：数据清理');
```

## 🧩 核心组件说明

### SAM 接口

SAM (Single Abstract Method) 接口是一个统一的功能接口，它继承了 `Runnable`, `Consumer`, `Supplier`, `Function`, `Predicate` 等函数式接口，使得 Groovy 脚本可以灵活地实现各种功能。

```java
@FunctionalInterface
public interface SAM<T, R> extends Runnable, Consumer<T>, Supplier<R>, Function<T, R>, Predicate<T> {
    R execute(T param);
}
```

### RefreshableScope

自定义的 Spring 作用域，用于管理动态创建的 Bean。它维护了一个单例缓存和销毁回调缓存，确保动态 Bean 的正确生命周期管理。

### RefreshBeanEvent 和 RefreshBeanEventListener

基于 Spring 事件机制，当对动态 Bean 进行增删改操作时，会发布相应的事件，由监听器负责实际的 Bean 注册和销毁操作。

### GroovyShellFactory

负责创建 GroovyShell 实例，其中绑定了 `ioc` (ApplicationContext) 和 `locals` (ThreadLocal) 两个内置变量，使得脚本可以访问 Spring 容器和线程局部变量。

### ScheduledTaskRegistrar

动态定时任务注册器，负责将定时任务与对应的 Dynamic Bean 绑定，并使用 ThreadPoolTaskScheduler 进行调度。

## 📖 API 参考

### RefreshableBeanModel

```java
public record RefreshableBeanModel(Long id, String beanName, String script, String description)
```

- `id`: 主键
- `beanName`: Bean 在内存中的名称
- `script`: SAM 类源码 (Groovy 脚本)
- `description`: 描述信息

### ScheduledTaskDefinition

```java
public record ScheduledTaskDefinition(Long id, String beanName, String cronExpression, String registryKey,
                                      String description)
```

- `id`: 主键
- `beanName`: 关联的 Dynamic Bean 名称
- `cronExpression`: Cron 表达式
- `registryKey`: 任务唯一标识符
- `description`: 描述信息

## 🛠️ 开发指南

### 构建项目

```bash
mvn clean install
```

### 运行示例

```bash
# 进入示例应用目录
cd dynamic-project-spring-boot-sample

# 启动应用
mvn spring-boot:run
```

### 测试 API

使用 HTTP 客户端测试 API：

```http
### 获取内存中所有已注册的 refreshableBean 类型的 beanName
GET http://localhost:8080/dynamicBean/beanNames

### 获取数据库中的所有 refreshableBean 列表
GET http://localhost:8080/dynamicBean

### 新增 bean
POST http://localhost:8080/dynamicBean
Content-Type: application/json

{
  "beanName": "test1",
  "script": "return { param -> println 'test1 running ...' } as SAM",
  "description": "test任务"
}

### 运行 bean
GET http://localhost:8080/dynamicBean/execute/test1

### 查询所有动态定时任务
GET http://localhost:8080/dynamicSchedule

### 新增动态定时任务
POST http://localhost:8080/dynamicSchedule
Content-Type: application/json

{
  "beanName": "print-a",
  "cronExpression": "0/10 * * * * ? ",
  "registryKey": "test1",
  "description": "动态定时任务：测试1"
}
```

## ⚠️ 注意事项

1. **动态 Bean 脚本要求**:
   - 脚本必须是有效的 Groovy 代码
   - 必须返回一个 SAM 实例
   - 可以使用 `ioc` 变量访问 Spring ApplicationContext
   - 可以使用 `locals` 变量传递线程局部变量

2. **动态定时任务要求**:
   - `beanName` 必须对应一个已存在的 Dynamic Bean
   - `cronExpression` 必须是有效的 Cron 表达式
   - `registryKey` 必须全局唯一

3. **数据库配置**:
   - 确保数据库连接信息正确配置
   - 确保数据库表结构已创建
   - 建议使用 HikariCP 连接池以获得最佳性能

4. **生产环境建议**:
   - 对 Groovy 脚本进行安全审查
   - 限制脚本执行时间和资源使用
   - 添加适当的监控和日志记录
   - 考虑添加脚本版本管理和回滚机制

## 📜 许可证

本项目采用 Apache 2.0 许可证。详见 [LICENSE](LICENSE) 文件。

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！