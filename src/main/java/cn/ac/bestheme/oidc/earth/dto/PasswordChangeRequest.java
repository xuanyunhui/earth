package cn.ac.bestheme.oidc.earth.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@RegisterForReflection
@Schema(description = "密码修改请求")
public record PasswordChangeRequest(
    @Schema(description = "当前密码", example = "oldpassword", required = true)
    String currentPassword,
    
    @Schema(description = "新密码", example = "newpassword", required = true)
    String newPassword
) {
} 