package cn.ac.bestheme.oidc.earth.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@RegisterForReflection
@Schema(description = "创建用户请求")
public record UserCreateRequest(
    @Schema(description = "用户名", example = "john_doe", required = true)
    String username,
    
    @Schema(description = "邮箱地址", example = "john.doe@example.com", required = true)
    String email,
    
    @Schema(description = "密码", example = "password123", required = true)
    String password,
    
    @Schema(description = "名", example = "John")
    String firstName,
    
    @Schema(description = "姓", example = "Doe")
    String lastName
) {
} 