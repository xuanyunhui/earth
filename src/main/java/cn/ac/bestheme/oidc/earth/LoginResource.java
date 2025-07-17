package cn.ac.bestheme.oidc.earth;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import io.quarkus.oidc.client.OidcClient;
import io.quarkus.oidc.client.Tokens;
import jakarta.inject.Inject;
import java.util.Map;

@Path("/auth/login")
@Tag(name = "登录接口")
public class LoginResource {

    @Inject
    OidcClient oidcClient;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "用户名密码登录，获取token并写入cookie")
    @APIResponse(responseCode = "200", description = "登录成功，access_token写入cookie", content = @Content(schema = @Schema(implementation = String.class)))
    public Response login(@RequestBody(required = true) Map<String, String> loginRequest) {
        try {
            Tokens token = oidcClient.getTokens(
                Map.of(
                    "username", loginRequest.get("username"),
                    "password", loginRequest.get("password")
                )
            ).await().indefinitely();
            if (token != null && token.getAccessToken() != null) {
                NewCookie cookie = new NewCookie.Builder("access_token")
                    .value(token.getAccessToken())
                    .path("/")
                    .maxAge(3600)
                    .httpOnly(true)
                    .secure(false)
                    .build();
                return Response.ok("登录成功").cookie(cookie).build();
            } else {
                return Response.status(401).entity("登录失败: 未获取到access_token").build();
            }
        } catch (Exception e) {
            return Response.status(500).entity("登录异常: " + e.getMessage()).build();
        }
    }
}
