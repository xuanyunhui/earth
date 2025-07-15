# Earth用户管理系统 - API使用指南

## 概述

Earth用户管理系统提供了完整的REST API，支持用户的增删改查操作。所有API都遵循OpenAPI 3.0规范，并提供了交互式的Swagger UI界面。

## API端点

### 基础信息
- **基础URL**: `http://localhost:8080`
- **API前缀**: `/api`
- **文档地址**: `http://localhost:8080/swagger-ui`
- **OpenAPI规范**: `http://localhost:8080/openapi`

## 用户管理API

### 1. 获取所有用户

**GET** `/api/users`

获取系统中所有用户的列表。

**响应示例**:
```json
[
  {
    "id": 1,
    "username": "john_doe",
    "email": "john.doe@example.com",
    "fullName": "John Doe",
    "status": "ACTIVE"
  }
]
```

### 2. 根据ID获取用户

**GET** `/api/users/{id}`

根据用户ID获取特定用户的详细信息。

**路径参数**:
- `id` (Long): 用户ID

**响应示例**:
```json
{
  "id": 1,
  "username": "john_doe",
  "email": "john.doe@example.com",
  "fullName": "John Doe",
  "status": "ACTIVE"
}
```

### 3. 创建新用户

**POST** `/api/users`

创建新的用户账户。

**请求体**:
```json
{
  "username": "new_user",
  "email": "new.user@example.com",
  "fullName": "New User"
}
```

**响应示例**:
```json
{
  "id": 2,
  "username": "new_user",
  "email": "new.user@example.com",
  "fullName": "New User",
  "status": "ACTIVE"
}
```

### 4. 更新用户信息

**PUT** `/api/users/{id}`

更新指定用户的信息。

**路径参数**:
- `id` (Long): 用户ID

**请求体**:
```json
{
  "username": "updated_user",
  "email": "updated.user@example.com",
  "fullName": "Updated User"
}
```

### 5. 删除用户

**DELETE** `/api/users/{id}`

删除指定的用户账户。

**路径参数**:
- `id` (Long): 用户ID

**响应**: 204 No Content

### 6. 搜索用户

**GET** `/api/users/search?q={query}`

根据用户名或邮箱搜索用户。

**查询参数**:
- `q` (String): 搜索关键词

**响应示例**:
```json
[
  {
    "id": 1,
    "username": "john_doe",
    "email": "john.doe@example.com",
    "fullName": "John Doe",
    "status": "ACTIVE"
  }
]
```

## 健康检查API

### 系统健康检查

**GET** `/health`

检查系统运行状态。

**响应示例**:
```json
{
  "status": "UP",
  "message": "Earth用户管理系统运行正常",
  "timestamp": "2024-01-01T12:00:00",
  "version": "1.0.0"
}
```

## 错误处理

所有API都遵循统一的错误响应格式：

```json
{
  "message": "错误描述信息",
  "code": "ERROR_CODE"
}
```

### 常见错误代码

- `USER_NOT_FOUND`: 用户不存在
- `INVALID_USERNAME`: 用户名无效
- `DUPLICATE_USERNAME`: 用户名重复

## 使用示例

### cURL示例

1. **获取所有用户**:
```bash
curl -X GET http://localhost:8080/api/users
```

2. **创建新用户**:
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "fullName": "Test User"
  }'
```

3. **获取特定用户**:
```bash
curl -X GET http://localhost:8080/api/users/1
```

4. **更新用户**:
```bash
curl -X PUT http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Updated Name"
  }'
```

5. **删除用户**:
```bash
curl -X DELETE http://localhost:8080/api/users/1
```

### JavaScript示例

```javascript
// 获取所有用户
fetch('/api/users')
  .then(response => response.json())
  .then(users => console.log(users));

// 创建新用户
fetch('/api/users', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
  },
  body: JSON.stringify({
    username: 'newuser',
    email: 'newuser@example.com',
    fullName: 'New User'
  })
})
.then(response => response.json())
.then(user => console.log(user));
```

## 数据模型

### User
```json
{
  "id": "Long",
  "username": "String",
  "email": "String",
  "fullName": "String",
  "status": "String (ACTIVE|INACTIVE|SUSPENDED)"
}
```

### UserCreateRequest
```json
{
  "username": "String (required)",
  "email": "String",
  "fullName": "String"
}
```

### UserUpdateRequest
```json
{
  "username": "String",
  "email": "String",
  "fullName": "String"
}
```

### ErrorResponse
```json
{
  "message": "String",
  "code": "String"
}
```

## 最佳实践

1. **错误处理**: 始终检查HTTP状态码和错误响应
2. **数据验证**: 在发送请求前验证数据格式
3. **分页**: 对于大量数据，考虑实现分页机制
4. **缓存**: 对于频繁访问的数据，考虑使用缓存
5. **日志**: 记录重要的API调用和错误信息

## 扩展功能

未来版本将支持以下功能：

- 用户角色管理
- 权限控制
- 批量操作
- 数据导入导出
- 审计日志
- 实时通知 