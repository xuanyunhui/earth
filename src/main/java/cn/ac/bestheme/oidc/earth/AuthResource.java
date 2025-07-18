package cn.ac.bestheme.oidc.earth;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import io.quarkus.oidc.client.Tokens;
import io.quarkus.oidc.client.OidcClients;
import jakarta.inject.Inject;
import java.util.Map;
import java.util.HashMap;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Path("/auth")
@Tag(name = "认证接口")
public class AuthResource {

    @Inject
    OidcClients oidcClients;

    @Inject
    JsonWebToken jwt;

    @ConfigProperty(name = "quarkus.oidc-client.broker.auth-server-url")
    String brokerAuthServerUrl;

    @ConfigProperty(name = "quarkus.oidc-client.broker.client-id")
    String brokerClientId;

    @ConfigProperty(name = "app.redirect-uri")
    String brokerRedirectURL;

    @ConfigProperty(name = "app.frontend-callback-url")
    String frontendCallbackUrl;

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "用户名密码登录，返回包含token的重定向URL")
    @APIResponse(responseCode = "200", description = "登录成功，返回包含token的fragment URL", content = @Content(schema = @Schema(implementation = Map.class)))
    public Response login(@RequestBody(required = true) Map<String, String> loginRequest) {
        try {
            // 通过 OIDC Client 获取 token
            Tokens token = oidcClients.getClient().getTokens(
                Map.of(
                    "username", loginRequest.get("username"),
                    "password", loginRequest.get("password")
                )
            ).await().indefinitely();
            
            if (token != null && token.getAccessToken() != null) {
                // 构建包含 token 的 fragment URL
                String redirectUrl = buildFragmentUrl(token.getAccessToken(), token.getRefreshToken());
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("redirectUrl", redirectUrl);
                response.put("message", "登录成功，请重定向到指定URL");
                
                return Response.ok(response).build();
            } else {
                return Response.status(401).entity(Map.of("success", false, "message", "登录失败: 用户名或密码错误")).build();
            }
        } catch (Exception e) {
            return Response.status(500).entity(Map.of("success", false, "message", "登录异常: " + e.getMessage())).build();
        }
    }

    @GET
    @Path("/github/login")
    @Operation(summary = "GitHub OAuth 登录，返回授权URL")
    @APIResponse(responseCode = "200", description = "返回GitHub授权URL", content = @Content(schema = @Schema(implementation = Map.class)))
    public Response githubLogin() {
        try {
            // 构建 Keycloak GitHub broker 授权URL
            String keycloakAuthUrl = buildGithubAuthUrl();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("authUrl", keycloakAuthUrl);
            response.put("message", "请重定向到GitHub授权页面");
            
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(500).entity(Map.of("success", false, "message", "GitHub登录异常: " + e.getMessage())).build();
        }
    }

    @GET
    @Path("/callback")
    @Operation(summary = "OIDC 回调处理 (GET)，处理 Keycloak broker 重定向")
    @APIResponse(responseCode = "200", description = "登录成功，返回包含token的fragment URL", content = @Content(schema = @Schema(implementation = Map.class)))
    @APIResponse(responseCode = "400", description = "缺少授权码")
    @APIResponse(responseCode = "401", description = "授权码无效")
    @APIResponse(responseCode = "500", description = "服务器错误")
    public Response oauthCallbackGet(@jakarta.ws.rs.QueryParam("code") String authorizationCode,
                                     @jakarta.ws.rs.QueryParam("state") String state) {
        try {
            if (authorizationCode == null || authorizationCode.isEmpty()) {
                return Response.status(400).entity(Map.of("success", false, "message", "缺少授权码")).build();
            }
            
            // 通过 OIDC Client 使用授权码交换 token
            Tokens token = oidcClients.getClient("broker").getTokens(
                Map.of(
                    "code", authorizationCode,
                    "redirect_uri", brokerRedirectURL
                )
            ).await().indefinitely();
            
            if (token != null && token.getAccessToken() != null) {
                // 构建包含 token 的 fragment URL，逻辑和 /auth/login 一样
                String redirectUrl = buildFragmentUrl(token.getAccessToken(), token.getRefreshToken());
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("redirectUrl", redirectUrl);
                response.put("message", "登录成功，请重定向到指定URL");
                
                return Response.ok(response).build();
            } else {
                return Response.status(401).entity(Map.of("success", false, "message", "登录失败: 授权码无效")).build();
            }
        } catch (Exception e) {
            return Response.status(500).entity(Map.of("success", false, "message", "回调处理异常: " + e.getMessage())).build();
        }
    }
    
    @POST
    @Path("/refresh")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "使用refresh_token刷新access_token，返回新的fragment URL")
    @APIResponse(responseCode = "200", description = "刷新成功，返回新的fragment URL", content = @Content(schema = @Schema(implementation = Map.class)))
    @APIResponse(responseCode = "400", description = "缺少refresh_token")
    @APIResponse(responseCode = "401", description = "refresh_token无效")
    @APIResponse(responseCode = "500", description = "服务器错误")
    public Response refreshToken(@RequestBody(required = true) Map<String, String> request) {
        try {
            String refreshToken = request.get("refresh_token");
            if (refreshToken == null || refreshToken.isEmpty()) {
                return Response.status(400).entity(Map.of("success", false, "message", "缺少refresh_token")).build();
            }
            // 通过 OIDC Client 刷新 token
            Tokens token = oidcClients.getClient("broker").getTokens(
                Map.of(
                    "refresh_token", refreshToken
                )
            ).await().indefinitely();
            if (token != null && token.getAccessToken() != null) {
                String redirectUrl = buildFragmentUrl(token.getAccessToken(), token.getRefreshToken());
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("redirectUrl", redirectUrl);
                response.put("message", "刷新成功，请重定向到指定URL");
                return Response.ok(response).build();
            } else {
                return Response.status(401).entity(Map.of("success", false, "message", "refresh_token无效或已过期")).build();
            }
        } catch (Exception e) {
            return Response.status(500).entity(Map.of("success", false, "message", "刷新异常: " + e.getMessage())).build();
        }
    }

    private String buildFragmentUrl(String accessToken, String refreshToken) {
        // 构建包含 token 的 fragment URL，前端可以从这个 URL 的 #fragment 中获取 token
        String baseUrl = frontendCallbackUrl;
        StringBuilder fragmentUrl = new StringBuilder(baseUrl);
        fragmentUrl.append("#access_token=").append(accessToken);
        fragmentUrl.append("&token_type=Bearer");
        if (refreshToken != null && !refreshToken.isEmpty()) {
            fragmentUrl.append("&refresh_token=").append(refreshToken);
        }
        // 添加过期时间（假设1小时）
        fragmentUrl.append("&expires_in=3600");
        fragmentUrl.append("&scope=openid profile email");
        return fragmentUrl.toString();
    }

    private String buildGithubAuthUrl() {
        // 构建 Keycloak GitHub broker 授权URL，使用配置中的信息
        try {
            StringBuilder authUrl = new StringBuilder();
            authUrl.append(brokerAuthServerUrl)
                   .append("/protocol/openid-connect/auth"); // 使用标准的 OIDC 认证端点
            
            authUrl.append("?client_id=").append(brokerClientId)
                   .append("&redirect_uri=").append(URLEncoder.encode(brokerRedirectURL, StandardCharsets.UTF_8))
                   .append("&response_type=code")
                   .append("&scope=openid")
                   .append("&kc_idp_hint=github") // 指定使用 GitHub identity provider
                   .append("&state=").append(System.currentTimeMillis()); // 简单的 state 参数
            
            return authUrl.toString();
        } catch (Exception e) {
            throw new RuntimeException("构建 GitHub 授权 URL 失败", e);
        }
    }

    @GET
    @Path("/me")
    @Operation(summary = "获取当前用户信息")
    @APIResponse(responseCode = "200", description = "成功获取用户信息", content = @Content(schema = @Schema(implementation = Map.class)))
    @APIResponse(responseCode = "401", description = "未登录或token无效")
    public Response getCurrentUser() {
        try {
            // 使用 MicroProfile JWT 自动解析 Authorization 头中的 token
            if (jwt == null || jwt.getSubject() == null) {
                return Response.status(401).entity(Map.of("error", "未找到访问令牌", "message", "请在Authorization头中提供Bearer token")).build();
            }
            
            // 使用 MicroProfile JWT 获取用户信息
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("sub", jwt.getSubject());
            userInfo.put("preferred_username", jwt.getClaim("preferred_username"));
            userInfo.put("email", jwt.getClaim("email"));
            userInfo.put("name", jwt.getClaim("name"));
            userInfo.put("given_name", jwt.getClaim("given_name"));
            userInfo.put("family_name", jwt.getClaim("family_name"));
            userInfo.put("email_verified", jwt.getClaim("email_verified"));
            
            return Response.ok(userInfo).build();
        } catch (Exception e) {
            return Response.status(500).entity(Map.of("error", "获取用户信息异常", "message", e.getMessage())).build();
        }
    }

    @POST
    @Path("/logout")
    @Operation(summary = "用户登出")
    @APIResponse(responseCode = "200", description = "登出成功", content = @Content(schema = @Schema(implementation = Map.class)))
    @APIResponse(responseCode = "401", description = "未登录或token无效")
    public Response logout() {
        try {
            // 使用 MicroProfile JWT 自动解析的 token
            if (jwt == null || jwt.getSubject() == null) {
                return Response.status(401).entity(Map.of("error", "未找到访问令牌", "message", "请在Authorization头中提供Bearer token")).build();
            }
            
            // 获取用户信息用于响应
            String username = jwt.getClaim("preferred_username");
            String userId = jwt.getSubject();
            
            // 从 JWT 获取原始 token
            String rawToken = jwt.getClaim("raw_token");
            if (rawToken == null) {
                // 如果 raw_token claim 不存在，直接使用注入的 JWT 的原始令牌
                rawToken = jwt.toString();
            }
            
            // 调用 Keycloak revoke 端点撤销 token
            boolean revokeSuccess = revokeToken(rawToken);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", revokeSuccess);
            response.put("message", revokeSuccess ? "登出成功，token已撤销" : "登出成功，但token撤销失败");
            response.put("user", username);
            response.put("userId", userId);
            
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(500).entity(Map.of("error", "登出异常", "message", e.getMessage())).build();
        }
    }
    
    private boolean revokeToken(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        
        try {
            // 使用 OidcClient 撤销 token
            oidcClients.getClient("broker").revokeAccessToken(token).await().indefinitely();
            return true;
        } catch (Exception e) {
            System.err.println("Token revocation failed: " + e.getMessage());
            return false;
        }
    }
}
