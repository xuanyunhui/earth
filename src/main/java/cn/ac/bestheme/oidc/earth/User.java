package cn.ac.bestheme.oidc.earth;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@RegisterForReflection
@Schema(description = "用户信息")
public record User(
    @Schema(description = "用户ID", example = "1")
    Long id,
    
    @Schema(description = "用户名", example = "john_doe")
    String username,
    
    @Schema(description = "邮箱地址", example = "john.doe@example.com")
    String email,
    
    @Schema(description = "全名", example = "John Doe")
    String fullName,
    
    @Schema(description = "用户状态", example = "ACTIVE")
    String status
) {
} 