#!/bin/bash

# Earth用户管理系统 - API测试脚本

echo "🧪 开始测试Earth用户管理系统API..."

BASE_URL="http://localhost:8080"

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 测试函数
test_endpoint() {
    local name="$1"
    local method="$2"
    local url="$3"
    local data="$4"
    
    echo -n "测试 $name... "
    
    if [ -n "$data" ]; then
        response=$(curl -s -w "%{http_code}" -X "$method" "$url" -H "Content-Type: application/json" -d "$data")
    else
        response=$(curl -s -w "%{http_code}" -X "$method" "$url")
    fi
    
    http_code="${response: -3}"
    body="${response%???}"
    
    if [ "$http_code" -ge 200 ] && [ "$http_code" -lt 300 ]; then
        echo -e "${GREEN}✅ 成功 (HTTP $http_code)${NC}"
        if [ -n "$body" ]; then
            echo "   响应: $body"
        fi
    else
        echo -e "${RED}❌ 失败 (HTTP $http_code)${NC}"
        echo "   响应: $body"
    fi
    echo
}

# 等待应用启动
echo "⏳ 等待应用启动..."
sleep 10

# 测试健康检查
test_endpoint "健康检查" "GET" "$BASE_URL/health"

# 测试认证API
echo "🔐 测试认证API..."

test_endpoint "用户注册" "POST" "$BASE_URL/api/auth/register" '{"username":"testuser","email":"test@example.com","password":"password123","firstName":"Test","lastName":"User"}'

test_endpoint "用户登录" "POST" "$BASE_URL/api/auth/login" '{"username":"testuser","password":"password123"}'

test_endpoint "忘记密码" "POST" "$BASE_URL/api/auth/forgot-password" '{"email":"test@example.com"}'

test_endpoint "重置密码" "POST" "$BASE_URL/api/auth/reset-password" '{"token":"reset-token","newPassword":"newpassword123"}'

test_endpoint "用户登出" "POST" "$BASE_URL/api/auth/logout"

# 测试用户管理API
echo "👤 测试用户管理API..."

test_endpoint "获取当前用户" "GET" "$BASE_URL/api/users/me"

test_endpoint "更新用户资料" "PUT" "$BASE_URL/api/users/me" '{"firstName":"Updated","lastName":"Name","email":"updated@example.com"}'

test_endpoint "修改密码" "PUT" "$BASE_URL/api/users/me/password" '{"currentPassword":"oldpassword","newPassword":"newpassword"}'

# 测试管理员API
echo "👨‍💼 测试管理员API..."

test_endpoint "获取用户列表（管理员）" "GET" "$BASE_URL/api/admin/users"

test_endpoint "创建用户（管理员）" "POST" "$BASE_URL/api/admin/users" '{"username":"adminuser","email":"adminuser@example.com","password":"password123","firstName":"Admin","lastName":"User"}'

# 获取创建的用户ID（这里简化处理）
USER_ID="87de9ead-123d-4022-bb53-146e5b31db67"
test_endpoint "获取用户详情（管理员）" "GET" "$BASE_URL/api/admin/users/$USER_ID"

test_endpoint "更新用户（管理员）" "PUT" "$BASE_URL/api/admin/users/$USER_ID" '{"firstName":"Updated","lastName":"User","enabled":true}'

test_endpoint "分配角色（管理员）" "PUT" "$BASE_URL/api/admin/users/$USER_ID/roles" '["user","admin"]'

test_endpoint "删除用户（管理员）" "DELETE" "$BASE_URL/api/admin/users/$USER_ID"

# 测试Swagger UI
echo -n "测试 Swagger UI... "
if curl -s -I "$BASE_URL/swagger-ui/" | grep -q "200 OK"; then
    echo -e "${GREEN}✅ 可访问${NC}"
else
    echo -e "${RED}❌ 不可访问${NC}"
fi

# 测试OpenAPI规范
echo -n "测试 OpenAPI规范... "
if curl -s "$BASE_URL/openapi" | grep -q "openapi:"; then
    echo -e "${GREEN}✅ 可访问${NC}"
else
    echo -e "${RED}❌ 不可访问${NC}"
fi

echo
echo "🎉 API测试完成！"
echo
echo "📚 访问地址："
echo "   - 应用主页: $BASE_URL"
echo "   - 健康检查: $BASE_URL/health"
echo "   - Swagger UI: $BASE_URL/swagger-ui/"
echo "   - OpenAPI规范: $BASE_URL/openapi"
echo "   - 认证API: $BASE_URL/api/auth"
echo "   - 用户管理API: $BASE_URL/api/users"
echo "   - 管理员API: $BASE_URL/api/admin/users"
echo
echo "🔑 Keycloak管理控制台: http://localhost:8180"
echo "   用户名: admin"
echo "   密码: admin" 