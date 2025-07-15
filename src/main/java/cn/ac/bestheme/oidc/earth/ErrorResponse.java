package cn.ac.bestheme.oidc.earth;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@RegisterForReflection
@Schema(description = "错误响应")
public record ErrorResponse(
    @Schema(description = "错误消息", example = "用户不存在")
    String message,
    
    @Schema(description = "错误代码", example = "USER_NOT_FOUND")
    String code
) {
} 