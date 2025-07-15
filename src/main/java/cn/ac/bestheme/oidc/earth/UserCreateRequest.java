package cn.ac.bestheme.oidc.earth;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@RegisterForReflection
@Schema(description = "创建用户请求")
public record UserCreateRequest(
    @Schema(description = "用户名", example = "john_doe", required = true)
    String username,
    
    @Schema(description = "邮箱地址", example = "john.doe@example.com")
    String email,
    
    @Schema(description = "全名", example = "John Doe")
    String fullName
) {
} 