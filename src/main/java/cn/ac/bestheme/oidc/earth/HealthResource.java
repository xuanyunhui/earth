package cn.ac.bestheme.oidc.earth;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Path("/health")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "健康检查", description = "系统健康状态检查")
public class HealthResource {

    @GET
    @Operation(
        summary = "系统健康检查",
        description = "检查系统运行状态和基本功能"
    )
    @APIResponse(
        responseCode = "200",
        description = "系统运行正常",
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON,
            schema = @Schema(implementation = HealthStatus.class)
        )
    )
    public Response health() {
        HealthStatus status = new HealthStatus(
            "UP",
            "Earth用户管理系统运行正常",
            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            "1.0.0"
        );
        return Response.ok(status).build();
    }

    @RegisterForReflection
    @Schema(description = "健康状态信息")
    public record HealthStatus(
        @Schema(description = "系统状态", example = "UP")
        String status,
        
        @Schema(description = "状态描述", example = "系统运行正常")
        String message,
        
        @Schema(description = "检查时间", example = "2024-01-01T12:00:00")
        String timestamp,
        
        @Schema(description = "应用版本", example = "1.0.0")
        String version
    ) {
    }
} 