package cn.ac.bestheme.oidc.earth;

import cn.ac.bestheme.oidc.earth.dto.*;
import cn.ac.bestheme.oidc.earth.service.KeycloakService;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.annotation.PostConstruct;
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

@Path("/api/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "认证管理", description = "用户认证相关操作")
public class AuthResource {

    private static final Logger LOG = Logger.getLogger(AuthResource.class);

    @Inject
    KeycloakService keycloakService;

    @PostConstruct
    void init() {
        keycloakService.init();
    }

    @POST
    @Path("/login")
    @Operation(
        summary = "用户登录",
        description = "使用用户名和密码进行登录，返回JWT令牌"
    )
    @APIResponses({
        @APIResponse(
            responseCode = "200",
            description = "登录成功，返回JWT令牌",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = AuthResponse.class)
            )
        ),
        @APIResponse(
            responseCode = "401",
            description = "用户名或密码错误"
        )
    })
    public Response login(
        @Parameter(description = "登录信息", required = true)
        LoginRequest request
    ) {
        try {
            AuthResponse authResponse = keycloakService.login(request);
            return Response.ok(authResponse).build();
        } catch (Exception e) {
            LOG.error("Login failed for user: " + request.username(), e);
            return Response.status(Response.Status.UNAUTHORIZED)
                .entity(new ErrorResponse("用户名或密码错误", "INVALID_CREDENTIALS"))
                .build();
        }
    }

    @POST
    @Path("/logout")
    @Authenticated
    @Operation(
        summary = "用户登出",
        description = "用户登出，清除会话"
    )
    @APIResponses({
        @APIResponse(
            responseCode = "204",
            description = "登出成功"
        ),
        @APIResponse(
            responseCode = "401",
            description = "未授权"
        )
    })
    public Response logout() {
        // 在Keycloak中，登出通常由客户端处理
        // 这里返回成功状态
        return Response.noContent().build();
    }

    @POST
    @Path("/register")
    @Operation(
        summary = "用户注册",
        description = "注册新用户账户"
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
        )
    })
    public Response register(
        @Parameter(description = "用户注册信息", required = true)
        UserCreateRequest request
    ) {
        try {
            UserDto user = keycloakService.createUser(request);
            return Response.status(Response.Status.CREATED).entity(user).build();
        } catch (Exception e) {
            LOG.error("Registration failed for user: " + request.username(), e);
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse("用户注册失败", "REGISTRATION_FAILED"))
                .build();
        }
    }

    @POST
    @Path("/forgot-password")
    @Operation(
        summary = "忘记密码",
        description = "请求密码重置邮件"
    )
    @APIResponses({
        @APIResponse(
            responseCode = "200",
            description = "重置邮件发送成功"
        ),
        @APIResponse(
            responseCode = "404",
            description = "用户不存在"
        )
    })
    public Response forgotPassword(
        @Parameter(description = "忘记密码请求", required = true)
        ForgotPasswordRequest request
    ) {
        try {
            // 这里应该实现发送重置邮件的逻辑
            // 由于Keycloak Dev Service的限制，这里只是模拟
            LOG.info("Password reset requested for email: " + request.email());
            return Response.ok()
                .entity(new ErrorResponse("重置邮件已发送", "RESET_EMAIL_SENT"))
                .build();
        } catch (Exception e) {
            LOG.error("Forgot password failed for email: " + request.email(), e);
            return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorResponse("用户不存在", "USER_NOT_FOUND"))
                .build();
        }
    }

    @POST
    @Path("/reset-password")
    @Operation(
        summary = "重置密码",
        description = "使用重置令牌设置新密码"
    )
    @APIResponses({
        @APIResponse(
            responseCode = "200",
            description = "密码更新成功"
        ),
        @APIResponse(
            responseCode = "400",
            description = "重置令牌无效"
        )
    })
    public Response resetPassword(
        @Parameter(description = "重置密码请求", required = true)
        ResetPasswordRequest request
    ) {
        try {
            // 这里应该实现验证重置令牌并更新密码的逻辑
            // 由于Keycloak Dev Service的限制，这里只是模拟
            LOG.info("Password reset with token: " + request.token());
            return Response.ok()
                .entity(new ErrorResponse("密码更新成功", "PASSWORD_UPDATED"))
                .build();
        } catch (Exception e) {
            LOG.error("Reset password failed", e);
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse("重置令牌无效", "INVALID_TOKEN"))
                .build();
        }
    }
} 