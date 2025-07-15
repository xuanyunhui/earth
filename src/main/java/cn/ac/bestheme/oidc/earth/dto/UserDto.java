package cn.ac.bestheme.oidc.earth.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;
import java.util.Map;

@RegisterForReflection
@Schema(description = "用户信息")
public record UserDto(
    @Schema(description = "用户ID", example = "123e4567-e89b-12d3-a456-426614174000")
    String id,
    
    @Schema(description = "用户名", example = "john_doe")
    String username,
    
    @Schema(description = "邮箱地址", example = "john.doe@example.com")
    String email,
    
    @Schema(description = "名", example = "John")
    String firstName,
    
    @Schema(description = "姓", example = "Doe")
    String lastName,
    
    @Schema(description = "是否启用", example = "true")
    Boolean enabled,
    
    @Schema(description = "创建时间戳", example = "1640995200000")
    Long createdTimestamp,
    
    @Schema(description = "用户属性，key为属性名，value为属性值列表")
    Map<String, List<String>> attributes
) {
} 