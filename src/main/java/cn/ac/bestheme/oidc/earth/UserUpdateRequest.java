package cn.ac.bestheme.oidc.earth;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@RegisterForReflection
@Schema(description = "更新用户请求")
public record UserUpdateRequest(
    @Schema(description = "用户名", example = "john_doe")
    String username,
    
    @Schema(description = "邮箱地址", example = "john.doe@example.com")
    String email,
    
    @Schema(description = "全名", example = "John Doe")
    String fullName
) {
} 