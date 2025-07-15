package cn.ac.bestheme.oidc.earth.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@RegisterForReflection
@Schema(description = "重置密码请求")
public record ResetPasswordRequest(
    @Schema(description = "重置令牌", example = "reset-token-123", required = true)
    String token,
    
    @Schema(description = "新密码", example = "newpassword", required = true)
    String newPassword
) {
} 