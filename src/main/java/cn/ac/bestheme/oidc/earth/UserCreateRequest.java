package cn.ac.bestheme.oidc.earth;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@RegisterForReflection
@Schema(description = "创建用户请求")
public record UserCreateRequest(
    @Schema(description = "用户名", examples = {"john_doe", "jane_smith", "admin"}, required = true)
    String username,
    
    @Schema(description = "邮箱地址", examples = {"john.doe@example.com", "jane.smith@example.com"})
    String email,
    
    @Schema(description = "全名", examples = {"John Doe", "Jane Smith"})
    String fullName
) {
} 