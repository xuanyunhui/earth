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

@Path("/api/admin/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "管理员", description = "管理员用户管理操作")
@Authenticated
@PermissionsAllowed("admin")
public class AdminResource {

    private static final Logger LOG = Logger.getLogger(AdminResource.class);

    @Inject
    KeycloakService keycloakService;

    @GET
    @PermissionsAllowed("admin:read")
    @Operation(
        summary = "获取用户列表（管理员）",
        description = "管理员获取所有用户的列表"
    )
    @APIResponses({
        @APIResponse(
            responseCode = "200",
            description = "用户列表",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = UserDto.class)
            )
        ),
        @APIResponse(
            responseCode = "401",
            description = "未授权"
        ),
        @APIResponse(
            responseCode = "403",
            description = "权限不足"
        )
    })
    public Response listUsers() {
        try {
            List<UserDto> users = keycloakService.getAllUsers();
            return Response.ok(users).build();
        } catch (Exception e) {
            LOG.error("Error listing users", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorResponse("获取用户列表失败", "LIST_USERS_FAILED"))
                .build();
        }
    }

    @POST
    @PermissionsAllowed("admin:create")
    @Operation(
        summary = "创建用户（管理员）",
        description = "管理员创建新用户"
    )
    @APIResponses({
        @APIResponse(
            responseCode = "201",
            description = "用户创建成功",
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
        ),
        @APIResponse(
            responseCode = "403",
            description = "权限不足"
        )
    })
    public Response createUser(
        @Parameter(description = "用户创建信息", required = true)
        UserCreateRequest request
    ) {
        try {
            UserDto user = keycloakService.createUser(request);
            return Response.status(Response.Status.CREATED).entity(user).build();
        } catch (Exception e) {
            LOG.error("Error creating user", e);
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse("用户创建失败", "CREATE_USER_FAILED"))
                .build();
        }
    }

    @GET
    @Path("/{id}")
    @PermissionsAllowed("admin:read")
    @Operation(
        summary = "根据ID获取用户",
        description = "管理员根据用户ID获取用户详细信息"
    )
    @APIResponses({
        @APIResponse(
            responseCode = "200",
            description = "用户详细信息",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = UserDto.class)
            )
        ),
        @APIResponse(
            responseCode = "401",
            description = "未授权"
        ),
        @APIResponse(
            responseCode = "403",
            description = "权限不足"
        ),
        @APIResponse(
            responseCode = "404",
            description = "用户不存在"
        )
    })
    public Response getUserById(
        @Parameter(description = "用户ID", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
        @PathParam("id") String id
    ) {
        try {
            UserDto user = keycloakService.getUserById(id);
            return Response.ok(user).build();
        } catch (Exception e) {
            LOG.error("Error getting user by ID: " + id, e);
            return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorResponse("用户不存在", "USER_NOT_FOUND"))
                .build();
        }
    }

    @PUT
    @Path("/{id}")
    @PermissionsAllowed("admin:update")
    @Operation(
        summary = "更新用户信息",
        description = "管理员更新指定用户的信息"
    )
    @APIResponses({
        @APIResponse(
            responseCode = "200",
            description = "用户信息更新成功",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = UserDto.class)
            )
        ),
        @APIResponse(
            responseCode = "401",
            description = "未授权"
        ),
        @APIResponse(
            responseCode = "403",
            description = "权限不足"
        ),
        @APIResponse(
            responseCode = "404",
            description = "用户不存在"
        )
    })
    public Response updateUser(
        @Parameter(description = "用户ID", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
        @PathParam("id") String id,
        @Parameter(description = "用户更新信息", required = true)
        UserUpdateRequest request
    ) {
        try {
            UserDto user = keycloakService.updateUser(id, request);
            return Response.ok(user).build();
        } catch (Exception e) {
            LOG.error("Error updating user: " + id, e);
            return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorResponse("用户不存在", "USER_NOT_FOUND"))
                .build();
        }
    }

    @DELETE
    @Path("/{id}")
    @PermissionsAllowed("admin:delete")
    @Operation(
        summary = "删除用户",
        description = "管理员删除指定用户"
    )
    @APIResponses({
        @APIResponse(
            responseCode = "204",
            description = "用户删除成功"
        ),
        @APIResponse(
            responseCode = "401",
            description = "未授权"
        ),
        @APIResponse(
            responseCode = "403",
            description = "权限不足"
        ),
        @APIResponse(
            responseCode = "404",
            description = "用户不存在"
        )
    })
    public Response deleteUser(
        @Parameter(description = "用户ID", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
        @PathParam("id") String id
    ) {
        try {
            keycloakService.deleteUser(id);
            return Response.noContent().build();
        } catch (Exception e) {
            LOG.error("Error deleting user: " + id, e);
            return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorResponse("用户不存在", "USER_NOT_FOUND"))
                .build();
        }
    }

    @PUT
    @Path("/{id}/roles")
    @PermissionsAllowed("admin:manage-roles")
    @Operation(
        summary = "分配用户角色",
        description = "管理员为用户分配角色"
    )
    @APIResponses({
        @APIResponse(
            responseCode = "204",
            description = "角色分配成功"
        ),
        @APIResponse(
            responseCode = "401",
            description = "未授权"
        ),
        @APIResponse(
            responseCode = "403",
            description = "权限不足"
        ),
        @APIResponse(
            responseCode = "404",
            description = "用户不存在"
        )
    })
    public Response assignRoles(
        @Parameter(description = "用户ID", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
        @PathParam("id") String id,
        @Parameter(description = "角色名称列表", required = true)
        List<String> roleNames
    ) {
        try {
            keycloakService.assignRoles(id, roleNames);
            return Response.noContent().build();
        } catch (Exception e) {
            LOG.error("Error assigning roles to user: " + id, e);
            return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorResponse("用户不存在", "USER_NOT_FOUND"))
                .build();
        }
    }
} 