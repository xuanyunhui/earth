package cn.ac.bestheme.oidc.earth;

import cn.ac.bestheme.oidc.earth.dto.*;
import cn.ac.bestheme.oidc.earth.service.KeycloakService;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;
import io.quarkus.security.Authenticated;
import io.quarkus.security.PermissionsAllowed;

import java.util.List;

@Path("/api/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "用户管理", description = "用户相关操作")
public class UserManagementResource {

    private static final Logger LOG = Logger.getLogger(UserManagementResource.class);

    @Inject
    KeycloakService keycloakService;

    @GET
    @Path("/me")
    @Authenticated
    @Operation(
        summary = "获取当前用户信息",
        description = "获取当前登录用户的详细信息"
    )
    @APIResponses({
        @APIResponse(
            responseCode = "200",
            description = "返回用户信息",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = UserDto.class)
            )
        ),
        @APIResponse(
            responseCode = "401",
            description = "未授权"
        )
    })
    public Response getCurrentUser() {
        try {
            // 这里应该从JWT令牌中获取用户信息
            // 由于简化，这里返回模拟数据
            UserDto user = new UserDto(
                "current-user-id",
                "currentuser",
                "current@example.com",
                "Current",
                "User",
                true,
                System.currentTimeMillis(),
                null
            );
            return Response.ok(user).build();
        } catch (Exception e) {
            LOG.error("Error getting current user", e);
            return Response.status(Response.Status.UNAUTHORIZED)
                .entity(new ErrorResponse("未授权", "UNAUTHORIZED"))
                .build();
        }
    }

    @PUT
    @Path("/me")
    @Authenticated
    @Operation(
        summary = "更新用户资料",
        description = "更新当前用户的个人资料"
    )
    @APIResponses({
        @APIResponse(
            responseCode = "200",
            description = "资料更新成功",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = UserDto.class)
            )
        ),
        @APIResponse(
            responseCode = "400",
            description = "请求参数错误"
        ),
        @APIResponse(
            responseCode = "401",
            description = "未授权"
        )
    })
    public Response updateProfile(
        @Parameter(description = "用户更新信息", required = true)
        UserUpdateRequest request
    ) {
        try {
            // 这里应该更新当前用户的资料
            // 由于简化，这里返回模拟数据
            UserDto user = new UserDto(
                "current-user-id",
                request.username() != null ? request.username() : "currentuser",
                request.email() != null ? request.email() : "current@example.com",
                request.firstName() != null ? request.firstName() : "Current",
                request.lastName() != null ? request.lastName() : "User",
                request.enabled() != null ? request.enabled() : true,
                System.currentTimeMillis(),
                null
            );
            return Response.ok(user).build();
        } catch (Exception e) {
            LOG.error("Error updating profile", e);
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse("更新失败", "UPDATE_FAILED"))
                .build();
        }
    }

    @PUT
    @Path("/me/password")
    @Authenticated
    @Operation(
        summary = "修改密码",
        description = "修改当前用户的密码"
    )
    @APIResponses({
        @APIResponse(
            responseCode = "204",
            description = "密码修改成功"
        ),
        @APIResponse(
            responseCode = "400",
            description = "当前密码错误"
        ),
        @APIResponse(
            responseCode = "401",
            description = "未授权"
        )
    })
    public Response changePassword(
        @Parameter(description = "密码修改信息", required = true)
        PasswordChangeRequest request
    ) {
        try {
            // 这里应该验证当前密码并更新新密码
            LOG.info("Password change requested for current user");
            return Response.noContent().build();
        } catch (Exception e) {
            LOG.error("Error changing password", e);
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse("当前密码错误", "INVALID_CURRENT_PASSWORD"))
                .build();
        }
    }
} 