# Earth用户管理系统 - 安全权限控制

## 概述

Earth用户管理系统基于Quarkus + Keycloak实现，采用基于角色的访问控制（RBAC）模型，确保API接口的安全性。

## 权限模型

### 角色定义

1. **admin** - 管理员角色
   - 拥有所有权限
   - 可以管理所有用户
   - 可以分配角色

2. **user** - 普通用户角色
   - 只能管理自己的信息
   - 不能访问管理员功能

### 权限级别

#### 1. 公开接口（无需认证）
- `POST /api/auth/register` - 用户注册
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/forgot-password` - 忘记密码
- `POST /api/auth/reset-password` - 重置密码
- `GET /health` - 健康检查

#### 2. 用户接口（需要用户认证）
- `POST /api/auth/logout` - 用户登出
- `GET /api/users/me` - 获取当前用户信息
- `PUT /api/users/me` - 更新用户资料
- `PUT /api/users/me/password` - 修改密码

#### 3. 管理员接口（需要管理员权限）
- `GET /api/admin/users` - 获取用户列表
- `POST /api/admin/users` - 创建用户
- `GET /api/admin/users/{id}` - 获取用户详情
- `PUT /api/admin/users/{id}` - 更新用户
- `DELETE /api/admin/users/{id}` - 删除用户
- `PUT /api/admin/users/{id}/roles` - 分配角色

## 权限注解

### @Authenticated
- 要求用户已认证
- 适用于需要登录的接口

### @PermissionsAllowed
- 要求用户具有特定权限
- 支持细粒度权限控制

## 权限映射

| 权限名称 | 所需角色 | 描述 |
|---------|---------|------|
| `admin` | admin | 管理员基础权限 |
| `admin:read` | admin | 管理员读取权限 |
| `admin:create` | admin | 管理员创建权限 |
| `admin:update` | admin | 管理员更新权限 |
| `admin:delete` | admin | 管理员删除权限 |
| `admin:manage-roles` | admin | 管理员角色管理权限 |
| `user` | user, admin | 用户基础权限 |
| `user:read` | user, admin | 用户读取权限 |
| `user:update` | user, admin | 用户更新权限 |
| `user:change-password` | user, admin | 用户密码修改权限 |

## 安全配置

### application.properties
```properties
# 安全配置
app.security.enabled=true
```

### Keycloak配置
- 使用JWT令牌进行身份验证
- 支持角色和权限映射
- 集成OIDC协议

## 错误响应

### 401 Unauthorized
- 用户未认证
- 缺少有效的JWT令牌

### 403 Forbidden
- 用户已认证但权限不足
- 角色不匹配

### 示例错误响应
```json
{
  "message": "权限不足",
  "code": "INSUFFICIENT_PERMISSIONS",
  "timestamp": "2025-07-15T16:30:00.000Z"
}
```

## 测试权限

### 使用测试脚本
```bash
# 测试带认证的API
./scripts/test-api-with-auth.sh

# 测试不带认证的API
./scripts/test-api.sh
```

### 手动测试
1. 先调用登录接口获取JWT令牌
2. 在后续请求中添加 `Authorization: Bearer <token>` 头
3. 验证不同角色的权限访问

## 最佳实践

1. **最小权限原则**：只授予必要的权限
2. **角色分离**：管理员和普通用户权限明确分离
3. **令牌安全**：JWT令牌有过期时间，定期刷新
4. **日志记录**：记录所有权限检查和安全事件
5. **错误处理**：不暴露敏感信息在错误消息中

## 扩展权限

如需添加新的权限，请：

1. 在 `SecurityConfig.java` 中添加权限映射
2. 在相应的资源类上添加 `@PermissionsAllowed` 注解
3. 更新Keycloak中的角色配置
4. 更新本文档

## 故障排除

### 常见问题

1. **401错误**：检查JWT令牌是否有效
2. **403错误**：检查用户角色是否匹配
3. **权限不生效**：确认Keycloak配置正确

### 调试模式
```properties
# 启用安全调试日志
quarkus.log.category."cn.ac.bestheme.oidc.earth.config".level=DEBUG
``` 