#!/bin/bash

# Earth用户管理系统 - API测试脚本（带认证）

echo "🧪 开始测试Earth用户管理系统API（带认证）..."

BASE_URL="http://localhost:8080"

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 全局变量存储认证信息
ACCESS_TOKEN=""
REFRESH_TOKEN=""

# 测试函数（带认证）
test_endpoint() {
    local name="$1"
    local method="$2"
    local url="$3"
    local data="$4"
    local auth_required="${5:-false}"
    
    echo -n "测试 $name... "
    
    # 构建curl命令
    local curl_cmd="curl -s -w \"%{http_code}\" -X \"$method\" \"$url\""
    
    # 添加认证头（如果需要且token存在）
    if [ "$auth_required" = "true" ] && [ -n "$ACCESS_TOKEN" ]; then
        curl_cmd="$curl_cmd -H \"Authorization: Bearer $ACCESS_TOKEN\""
    fi
    
    # 添加数据（如果有）
    if [ -n "$data" ]; then
        curl_cmd="$curl_cmd -H \"Content-Type: application/json\" -d '$data'"
    fi
    
    # 执行请求
    response=$(eval $curl_cmd)
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

# 登录并获取token
login_and_get_token() {
    echo -e "${BLUE}🔐 执行登录获取认证token...${NC}"
    
    local login_response=$(curl -s -X POST "$BASE_URL/api/auth/login" \
        -H "Content-Type: application/json" \
        -d '{"username":"testuser","password":"password123"}')
    
    # 解析响应获取token（假设响应格式为 {"access_token": "...", "refresh_token": "..."}）
    ACCESS_TOKEN=$(echo "$login_response" | grep -o '"access_token":"[^"]*"' | cut -d'"' -f4)
    REFRESH_TOKEN=$(echo "$login_response" | grep -o '"refresh_token":"[^"]*"' | cut -d'"' -f4)
    
    if [ -n "$ACCESS_TOKEN" ]; then
        echo -e "${GREEN}✅ 登录成功，获取到access_token${NC}"
        echo "   Access Token: ${ACCESS_TOKEN:0:20}..."
    else
        echo -e "${RED}❌ 登录失败，无法获取token${NC}"
        echo "   响应: $login_response"
    fi
    echo
}

# 等待应用启动
echo "⏳ 等待应用启动..."
sleep 10

# 测试健康检查（不需要认证）
test_endpoint "健康检查" "GET" "$BASE_URL/health" "" "false"

# 测试认证API
echo "🔐 测试认证API..."

test_endpoint "用户注册" "POST" "$BASE_URL/api/auth/register" '{"username":"testuser1","email":"test1@example.com","password":"password123","firstName":"Test1","lastName":"User1"}' "false"

test_endpoint "用户登录" "POST" "$BASE_URL/api/auth/login" '{"username":"testuser1","password":"password123"}' "false"

# 登录获取token
login_and_get_token

test_endpoint "忘记密码" "POST" "$BASE_URL/api/auth/forgot-password" '{"email":"test@example.com"}' "false"

test_endpoint "重置密码" "POST" "$BASE_URL/api/auth/reset-password" '{"token":"reset-token","newPassword":"newpassword123"}' "false"

# 测试用户管理API（需要认证）
echo "👤 测试用户管理API（需要认证）..."

test_endpoint "获取当前用户" "GET" "$BASE_URL/api/users/me" "" "true"

test_endpoint "更新用户资料" "PUT" "$BASE_URL/api/users/me" '{"firstName":"Updated","lastName":"Name","email":"updated@example.com"}' "true"

test_endpoint "修改密码" "PUT" "$BASE_URL/api/users/me/password" '{"currentPassword":"oldpassword","newPassword":"newpassword"}' "true"

test_endpoint "用户登出" "POST" "$BASE_URL/api/auth/logout" "" "true"

# 测试管理员API（需要认证）
echo "👨‍💼 测试管理员API（需要认证）..."

# 重新登录获取token（因为刚才登出了）
login_and_get_token

test_endpoint "获取用户列表（管理员）" "GET" "$BASE_URL/api/admin/users" "" "true"

test_endpoint "创建用户（管理员）" "POST" "$BASE_URL/api/admin/users" '{"username":"adminuser","email":"adminuser@example.com","password":"password123","firstName":"Admin","lastName":"User"}' "true"

# 获取创建的用户ID（这里简化处理）
USER_ID="87de9ead-123d-4022-bb53-146e5b31db67"
test_endpoint "获取用户详情（管理员）" "GET" "$BASE_URL/api/admin/users/$USER_ID" "" "true"

test_endpoint "更新用户（管理员）" "PUT" "$BASE_URL/api/admin/users/$USER_ID" '{"firstName":"Updated","lastName":"User","enabled":true}' "true"

test_endpoint "分配角色（管理员）" "PUT" "$BASE_URL/api/admin/users/$USER_ID/roles" '["user","admin"]' "true"

test_endpoint "删除用户（管理员）" "DELETE" "$BASE_URL/api/admin/users/$USER_ID" "" "true"

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