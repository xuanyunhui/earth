package cn.ac.bestheme.oidc.earth.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@RegisterForReflection
@Schema(description = "认证响应")
public record AuthResponse(
    @Schema(description = "访问令牌", example = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...")
    String accessToken,
    
    @Schema(description = "用户信息")
    UserDto user
) {
} 