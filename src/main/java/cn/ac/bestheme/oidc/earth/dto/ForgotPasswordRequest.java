package cn.ac.bestheme.oidc.earth.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@RegisterForReflection
@Schema(description = "忘记密码请求")
public record ForgotPasswordRequest(
    @Schema(description = "邮箱地址", example = "john.doe@example.com", required = true)
    String email
) {
} 