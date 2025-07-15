# Earth - 基于Quarkus + Keycloak的用户管理系统

## 项目概述

Earth是一个基于现代Java技术栈构建的企业级用户管理系统，采用Quarkus框架和Keycloak身份认证服务，提供安全、高效、可扩展的用户管理解决方案。

## 技术栈

### 后端技术
- **Quarkus 3.24.3** - 超快的云原生Java框架
- **Java 21** - 最新的LTS版本
- **Maven** - 项目构建和依赖管理
- **Keycloak** - 开源身份和访问管理解决方案

### 核心特性
- 🚀 **高性能** - 基于Quarkus的快速启动和低内存占用
- 🔐 **安全认证** - 集成Keycloak提供企业级身份管理
- ☁️ **云原生** - 支持容器化部署和云环境
- 📦 **容器化** - 提供多种Docker镜像构建方案
- 🔧 **可扩展** - 模块化架构设计

## 项目结构

```
earth/
├── src/
│   └── main/
│       ├── java/           # Java源代码
│       ├── resources/      # 配置文件和静态资源
│       │   └── application.properties
│       └── docker/         # Docker配置文件
│           ├── Dockerfile.jvm
│           ├── Dockerfile.legacy-jar
│           ├── Dockerfile.native
│           └── Dockerfile.native-micro
├── target/                 # 构建输出目录
├── pom.xml                # Maven项目配置
├── mvnw                   # Maven Wrapper (Linux/Mac)
├── mvnw.cmd              # Maven Wrapper (Windows)
└── README.md             # 项目说明文档
```

## 快速开始

### 环境要求
- Java 21+
- Maven 3.8+
- Docker (可选，用于容器化部署)
- Keycloak服务器

### 本地开发

1. **克隆项目**
   ```bash
   git clone <repository-url>
   cd earth
   ```

2. **启动开发模式**
   ```bash
   ./mvnw quarkus:dev
   ```

3. **访问应用**
   - 应用地址: http://localhost:8080
   - 健康检查: http://localhost:8080/health

### 构建项目

**JVM模式构建**
```bash
./mvnw clean package
```

**原生镜像构建**
```bash
./mvnw clean package -Pnative
```

### 容器化部署

项目提供了多种Docker构建方案：

1. **JVM模式** - 标准Java应用容器
   ```bash
   docker build -f src/main/docker/Dockerfile.jvm -t earth:jvm .
   ```

2. **原生模式** - 高性能原生镜像
   ```bash
   docker build -f src/main/docker/Dockerfile.native -t earth:native .
   ```

3. **微服务模式** - 轻量级微服务容器
   ```bash
   docker build -f src/main/docker/Dockerfile.native-micro -t earth:micro .
   ```

## 配置说明

### Keycloak集成配置

在 `application.properties` 中配置Keycloak连接：

```properties
# Keycloak服务器配置
quarkus.oidc.auth-server-url=http://localhost:8080/auth
quarkus.oidc.client-id=earth-client
quarkus.oidc.credentials.secret=your-client-secret

# 应用配置
quarkus.http.port=8080
quarkus.http.host=0.0.0.0
```

### 环境变量

支持通过环境变量进行配置：

- `KEYCLOAK_URL` - Keycloak服务器地址
- `CLIENT_ID` - 客户端ID
- `CLIENT_SECRET` - 客户端密钥
- `APP_PORT` - 应用端口

## 开发指南

### 添加新功能

1. 在 `src/main/java` 下创建相应的包结构
2. 实现业务逻辑
3. 添加单元测试
4. 更新文档

### 代码规范

- 遵循Java编码规范
- 使用Quarkus最佳实践
- 添加适当的注释和文档

## 部署指南

### 生产环境部署

1. **构建生产镜像**
   ```bash
   ./mvnw clean package -Pnative
   docker build -f src/main/docker/Dockerfile.native -t earth:prod .
   ```

2. **配置环境变量**
   ```bash
   export KEYCLOAK_URL=https://your-keycloak-server
   export CLIENT_ID=earth-prod-client
   export CLIENT_SECRET=your-production-secret
   ```

3. **启动容器**
   ```bash
   docker run -d \
     --name earth-app \
     -p 8080:8080 \
     -e KEYCLOAK_URL=$KEYCLOAK_URL \
     -e CLIENT_ID=$CLIENT_ID \
     -e CLIENT_SECRET=$CLIENT_SECRET \
     earth:prod
   ```

### Kubernetes部署

项目支持Kubernetes部署，相关配置文件位于 `k8s/` 目录。

## 监控和日志

### 健康检查
- 端点: `/health`
- 包含应用状态和依赖服务状态

### 指标监控
- 端点: `/metrics`
- 提供Prometheus格式的监控指标

### 日志配置
- 使用Quarkus内置日志系统
- 支持结构化日志输出
- 可配置日志级别和输出格式

## 故障排除

### 常见问题

1. **启动失败**
   - 检查Java版本是否为21+
   - 确认Maven依赖下载完整
   - 验证Keycloak连接配置

2. **认证问题**
   - 检查Keycloak服务器状态
   - 验证客户端配置
   - 确认用户权限设置

3. **性能问题**
   - 使用原生镜像提升性能
   - 调整JVM参数
   - 监控资源使用情况

## 贡献指南

1. Fork项目
2. 创建功能分支
3. 提交更改
4. 创建Pull Request

## 许可证

本项目采用 [MIT License](LICENSE) 许可证。

## 联系方式

- 项目维护者: [维护者信息]
- 邮箱: [联系邮箱]
- 项目地址: [项目URL]

---

**注意**: 这是一个正在开发中的项目，功能可能会持续更新和完善。
