# Earth - 基于Quarkus + Keycloak的用户管理系统

## 项目概述

Earth是一个基于现代Java技术栈构建的企业级用户管理系统，采用Quarkus框架和Keycloak身份认证服务，提供安全、高效、可扩展的用户管理解决方案。

## 技术栈

### 后端技术
- **Quarkus 3.24.3** - 超快的云原生Java框架
- **Java 21** - 最新的LTS版本
- **Maven** - 项目构建和依赖管理
- **Keycloak 24.0** - 开源身份和访问管理解决方案
- **Keycloak Dev Services** - 开发环境自动配置
- **Keycloak Admin Client** - Keycloak管理API客户端
- **RESTEasy Reactive** - 响应式REST API框架
- **SmallRye OpenAPI** - OpenAPI 3.0规范实现
- **Swagger UI** - API文档可视化界面

### 核心特性
- 🚀 **高性能** - 基于Quarkus的快速启动和低内存占用
- 🔐 **安全认证** - 集成Keycloak提供企业级身份管理
- 🗄️ **Keycloak存储** - 使用Keycloak作为用户数据存储后端
- 🔧 **Dev Services** - 开发环境自动启动Keycloak服务
- 📚 **API文档** - 自动生成OpenAPI 3.0规范文档
- 🎨 **可视化界面** - 集成Swagger UI提供交互式API文档
- ☁️ **云原生** - 支持容器化部署和云环境
- 📦 **容器化** - 提供多种Docker镜像构建方案
- 🔧 **可扩展** - 模块化架构设计

## 项目结构

```
earth/
├── src/
│   └── main/
│       ├── java/cn/ac/bestheme/oidc/earth/
│       │   ├── dto/           # 数据传输对象
│       │   ├── service/       # 业务逻辑服务
│       │   ├── util/          # 工具类
│       │   ├── config/        # 配置类
│       │   ├── AuthResource.java          # 认证API
│       │   ├── UserManagementResource.java # 用户管理API
│       │   ├── AdminResource.java         # 管理员API
│       │   └── HealthResource.java        # 健康检查API
│       ├── resources/      # 配置文件和静态资源
│       │   ├── application.properties
│       │   └── earth-realm.json           # Keycloak realm配置
│       └── docker/         # Docker配置文件
│           ├── Dockerfile.jvm
│           ├── Dockerfile.legacy-jar
│           ├── Dockerfile.native
│           └── Dockerfile.native-micro
├── scripts/               # 脚本文件
├── docs/                  # 文档
├── target/                # 构建输出目录
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
   
   **Linux/Mac**:
   ```bash
   ./scripts/start-dev.sh
   ```
   
   **Windows**:
   ```cmd
   scripts\start-dev.bat
   ```
   
   **或者直接使用Maven**:
   ```bash
   ./mvnw quarkus:dev
   ```

3. **访问应用**
   - 应用地址: http://localhost:8080
   - 健康检查: http://localhost:8080/health
   - API文档: http://localhost:8080/swagger-ui
   - OpenAPI规范: http://localhost:8080/openapi
   - Keycloak管理控制台: http://localhost:8180 (admin/admin)

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

### OpenAPI配置

项目已集成OpenAPI 3.0规范，提供以下功能：

- **自动API文档生成** - 基于代码注解自动生成API文档
- **Swagger UI界面** - 提供交互式API测试界面
- **OpenAPI规范文件** - 支持API设计工具集成

### Keycloak Dev Services配置

项目使用Keycloak Dev Services进行开发环境配置：

- **自动启动** - 开发模式下自动启动Keycloak容器
- **自动配置** - 自动创建Realm、Client和用户
- **管理控制台** - 提供Keycloak管理界面
- **数据持久化** - 用户数据存储在Keycloak中

### Keycloak集成配置

项目使用Keycloak Dev Services，配置已自动完成：

```properties
# Keycloak Dev Service配置
quarkus.keycloak.devservices.enabled=true
quarkus.keycloak.devservices.realm-name=earth-realm
quarkus.keycloak.devservices.client-id=earth-client

# Keycloak Admin Client配置
quarkus.keycloak.admin-client.server-url=http://localhost:8180
quarkus.keycloak.admin-client.realm=master
quarkus.keycloak.admin-client.username=admin
quarkus.keycloak.admin-client.password=admin

# OIDC配置
quarkus.oidc.auth-server-url=http://localhost:8180/realms/earth-realm
quarkus.oidc.client-id=earth-client
quarkus.oidc.credentials.secret=earth-secret
```

### 环境变量

支持通过环境变量进行配置：

- `KEYCLOAK_URL` - Keycloak服务器地址
- `CLIENT_ID` - 客户端ID
- `CLIENT_SECRET` - 客户端密钥
- `APP_PORT` - 应用端口

## 开发指南

### API开发规范

项目使用OpenAPI 3.0规范进行API设计，开发时请遵循以下规范：

1. **使用OpenAPI注解** - 为所有API端点添加完整的OpenAPI注解
2. **数据模型定义** - 使用`@Schema`注解定义请求和响应模型
3. **错误处理** - 定义标准的错误响应格式
4. **API分组** - 使用`@Tag`注解对API进行分组管理

### API文档

- **详细API文档**: [API使用指南](docs/API_GUIDE.md)
- **安全权限文档**: [安全权限控制](docs/SECURITY.md)
- **Swagger UI**: 启动应用后访问 `http://localhost:8080/swagger-ui`
- **OpenAPI规范**: 启动应用后访问 `http://localhost:8080/openapi`

### 添加新功能

1. 在 `src/main/java` 下创建相应的包结构
2. 实现业务逻辑
3. 添加OpenAPI注解和文档
4. 添加权限控制注解（如需要）
5. 添加单元测试
6. 更新API文档

### 权限控制

项目实现了基于角色的访问控制（RBAC）：

- **公开接口**: 无需认证（注册、登录等）
- **用户接口**: 需要用户认证（`@Authenticated`）
- **管理员接口**: 需要管理员权限（`@PermissionsAllowed("admin")`）

详细权限配置请参考 [安全权限控制文档](docs/SECURITY.md)。

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

### API文档和监控
- **Swagger UI**: `/swagger-ui` - 交互式API文档界面
- **OpenAPI规范**: `/openapi` - OpenAPI 3.0规范文件
- **健康检查**: `/health` - 应用状态和依赖服务状态
- **指标监控**: `/metrics` - Prometheus格式的监控指标

### API端点

#### 认证API (`/api/auth`)
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/logout` - 用户登出
- `POST /api/auth/register` - 用户注册
- `POST /api/auth/forgot-password` - 忘记密码
- `POST /api/auth/reset-password` - 重置密码

#### 用户管理API (`/api/users`)
- `GET /api/users/me` - 获取当前用户信息
- `PUT /api/users/me` - 更新用户资料
- `PUT /api/users/me/password` - 修改密码

#### 管理员API (`/api/admin/users`)
- `GET /api/admin/users` - 获取用户列表
- `POST /api/admin/users` - 创建用户
- `GET /api/admin/users/{id}` - 获取用户详情
- `PUT /api/admin/users/{id}` - 更新用户
- `DELETE /api/admin/users/{id}` - 删除用户
- `PUT /api/admin/users/{id}/roles` - 分配角色

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
