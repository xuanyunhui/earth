package cn.ac.bestheme.oidc.earth;

import io.quarkus.runtime.annotations.RegisterForReflection;
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
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Path("/api/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "用户管理", description = "用户相关的CRUD操作")
public class UserResource {

    private final ConcurrentHashMap<Long, User> users = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @GET
    @Operation(
        summary = "获取所有用户",
        description = "返回系统中所有用户的列表"
    )
    @APIResponses({
        @APIResponse(
            responseCode = "200",
            description = "成功获取用户列表",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = User.class)
            )
        ),
        @APIResponse(
            responseCode = "500",
            description = "服务器内部错误"
        )
    })
    public Response getAllUsers() {
        List<User> userList = users.values().stream().toList();
        return Response.ok(userList).build();
    }

    @GET
    @Path("/{id}")
    @Operation(
        summary = "根据ID获取用户",
        description = "根据用户ID获取特定用户的详细信息"
    )
    @APIResponses({
        @APIResponse(
            responseCode = "200",
            description = "成功获取用户信息",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = User.class)
            )
        ),
        @APIResponse(
            responseCode = "404",
            description = "用户不存在"
        )
    })
    public Response getUserById(
        @Parameter(description = "用户ID", required = true, example = "1")
        @PathParam("id") Long id
    ) {
        User user = users.get(id);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorResponse("用户不存在", "USER_NOT_FOUND"))
                .build();
        }
        return Response.ok(user).build();
    }

    @POST
    @Operation(
        summary = "创建新用户",
        description = "创建新的用户账户"
    )
    @APIResponses({
        @APIResponse(
            responseCode = "201",
            description = "用户创建成功",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = User.class)
            )
        ),
        @APIResponse(
            responseCode = "400",
            description = "请求参数错误"
        )
    })
    public Response createUser(
        @Parameter(description = "用户信息", required = true)
        UserCreateRequest request
    ) {
        if (request.username() == null || request.username().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse("用户名不能为空", "INVALID_USERNAME"))
                .build();
        }

        Long id = idGenerator.getAndIncrement();
        User user = new User(id, request.username(), request.email(), request.fullName(), "ACTIVE");
        users.put(id, user);

        return Response.status(Response.Status.CREATED).entity(user).build();
    }

    @PUT
    @Path("/{id}")
    @Operation(
        summary = "更新用户信息",
        description = "更新指定用户的信息"
    )
    @APIResponses({
        @APIResponse(
            responseCode = "200",
            description = "用户信息更新成功",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = User.class)
            )
        ),
        @APIResponse(
            responseCode = "404",
            description = "用户不存在"
        )
    })
    public Response updateUser(
        @Parameter(description = "用户ID", required = true, example = "1")
        @PathParam("id") Long id,
        @Parameter(description = "更新的用户信息", required = true)
        UserUpdateRequest request
    ) {
        User existingUser = users.get(id);
        if (existingUser == null) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorResponse("用户不存在", "USER_NOT_FOUND"))
                .build();
        }

        User updatedUser = new User(
            id,
            request.username() != null ? request.username() : existingUser.username(),
            request.email() != null ? request.email() : existingUser.email(),
            request.fullName() != null ? request.fullName() : existingUser.fullName(),
            existingUser.status()
        );
        users.put(id, updatedUser);

        return Response.ok(updatedUser).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(
        summary = "删除用户",
        description = "删除指定的用户账户"
    )
    @APIResponses({
        @APIResponse(
            responseCode = "204",
            description = "用户删除成功"
        ),
        @APIResponse(
            responseCode = "404",
            description = "用户不存在"
        )
    })
    public Response deleteUser(
        @Parameter(description = "用户ID", required = true, example = "1")
        @PathParam("id") Long id
    ) {
        User user = users.remove(id);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorResponse("用户不存在", "USER_NOT_FOUND"))
                .build();
        }
        return Response.noContent().build();
    }

    @GET
    @Path("/search")
    @Operation(
        summary = "搜索用户",
        description = "根据用户名或邮箱搜索用户"
    )
    @APIResponses({
        @APIResponse(
            responseCode = "200",
            description = "搜索成功",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = User.class)
            )
        )
    })
    public Response searchUsers(
        @Parameter(description = "搜索关键词", example = "john")
        @QueryParam("q") String query
    ) {
        if (query == null || query.trim().isEmpty()) {
            return Response.ok(users.values().stream().toList()).build();
        }

        List<User> results = users.values().stream()
            .filter(user -> user.username().toLowerCase().contains(query.toLowerCase()) ||
                          (user.email() != null && user.email().toLowerCase().contains(query.toLowerCase())))
            .toList();

        return Response.ok(results).build();
    }
} 