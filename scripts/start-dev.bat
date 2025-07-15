@echo off

REM Earth用户管理系统 - 开发环境启动脚本 (Windows)

echo 🚀 启动Earth用户管理系统开发环境...

REM 检查Java版本
echo 📋 检查Java版本...
java -version

REM 检查Maven版本
echo 📋 检查Maven版本...
call mvnw.cmd --version

REM 启动开发模式
echo 🔥 启动Quarkus开发模式...
echo 📚 API文档将在以下地址可用：
echo    - Swagger UI: http://localhost:8080/swagger-ui
echo    - OpenAPI规范: http://localhost:8080/openapi
echo    - 健康检查: http://localhost:8080/health
echo.

call mvnw.cmd quarkus:dev 