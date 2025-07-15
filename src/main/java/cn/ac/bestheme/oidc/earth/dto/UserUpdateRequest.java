package cn.ac.bestheme.oidc.earth.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@RegisterForReflection
@Schema(description = "更新用户请求")
public record UserUpdateRequest(
    @Schema(description = "用户名", example = "john_doe")
    String username,
    
    @Schema(description = "邮箱地址", example = "john.doe@example.com")
    String email,
    
    @Schema(description = "名", example = "John")
    String firstName,
    
    @Schema(description = "姓", example = "Doe")
    String lastName,
    
    @Schema(description = "是否启用", example = "true")
    Boolean enabled
) {
} 