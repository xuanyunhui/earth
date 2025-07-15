package cn.ac.bestheme.oidc.earth.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@RegisterForReflection
@Schema(description = "登录请求")
public record LoginRequest(
    @Schema(description = "用户名", example = "john_doe", required = true)
    String username,
    
    @Schema(description = "密码", example = "password123", required = true)
    String password
) {
} 