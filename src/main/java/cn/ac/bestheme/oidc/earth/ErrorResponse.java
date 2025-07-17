package cn.ac.bestheme.oidc.earth;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@RegisterForReflection
@Schema(description = "错误响应")
public record ErrorResponse(
    @Schema(description = "错误消息", examples = {"用户不存在", "参数错误"})
    String message,
    
    @Schema(description = "错误代码", examples = {"USER_NOT_FOUND", "INVALID_PARAMETER"})
    String code
) {
} 