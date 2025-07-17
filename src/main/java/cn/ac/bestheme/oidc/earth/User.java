package cn.ac.bestheme.oidc.earth;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@RegisterForReflection
@Schema(description = "用户信息")
public record User(
    @Schema(description = "用户ID", examples = {"1", "2", "3"})
    Long id,
    
    @Schema(description = "用户名", examples = {"john_doe", "jane_smith", "admin"})
    String username,
    
    @Schema(description = "邮箱地址", examples = {"john.doe@example.com", "jane.smith@example.com"})
    String email,
    
    @Schema(description = "全名", examples = {"John Doe", "Jane Smith"})
    String fullName,
    
    @Schema(description = "用户状态", examples = {"ACTIVE", "INACTIVE", "PENDING"})
    String status
) {
} 