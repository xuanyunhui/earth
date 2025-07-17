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
sleep 5

# 测试健康检查
test_endpoint "健康检查" "GET" "$BASE_URL/q/health"

# 测试获取所有用户（初始为空）
test_endpoint "获取所有用户" "GET" "$BASE_URL/api/users"

# 测试创建用户
test_endpoint "创建用户" "POST" "$BASE_URL/api/users" '{"username":"testuser","email":"test@example.com","fullName":"Test User"}'

# 测试获取特定用户
test_endpoint "获取用户ID=1" "GET" "$BASE_URL/api/users/1"

# 测试更新用户
test_endpoint "更新用户" "PUT" "$BASE_URL/api/users/1" '{"fullName":"Updated Test User"}'

# 测试搜索用户
test_endpoint "搜索用户" "GET" "$BASE_URL/api/users/search?q=test"

# 测试获取所有用户（应该有数据）
test_endpoint "获取所有用户（更新后）" "GET" "$BASE_URL/api/users"

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
echo "   - 健康检查: $BASE_URL/q/health"
echo "   - Swagger UI: $BASE_URL/swagger-ui/"
echo "   - OpenAPI规范: $BASE_URL/openapi"
echo "   - 用户API: $BASE_URL/api/users" 