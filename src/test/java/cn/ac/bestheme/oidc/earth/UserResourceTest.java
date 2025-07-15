package cn.ac.bestheme.oidc.earth;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

@QuarkusTest
public class UserResourceTest {

    @Test
    public void testHealthEndpoint() {
        given()
            .when().get("/health")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("status", equalTo("UP"))
            .body("message", containsString("运行正常"));
    }

    @Test
    public void testGetCurrentUser() {
        given()
            .when().get("/api/users/me")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("username", equalTo("currentuser"));
    }

    @Test
    public void testUpdateProfile() {
        String updateJson = """
            {
                "firstName": "Updated",
                "lastName": "Name",
                "email": "updated@example.com"
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(updateJson)
            .when().put("/api/users/me")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("firstName", equalTo("Updated"))
            .body("lastName", equalTo("Name"))
            .body("email", equalTo("updated@example.com"));
    }

    @Test
    public void testChangePassword() {
        String passwordJson = """
            {
                "currentPassword": "oldpassword",
                "newPassword": "newpassword"
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(passwordJson)
            .when().put("/api/users/me/password")
            .then()
            .statusCode(204);
    }

    @Test
    public void testLogin() {
        String loginJson = """
            {
                "username": "testuser",
                "password": "password123"
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(loginJson)
            .when().post("/api/auth/login")
            .then()
            .statusCode(401); // 用户不存在，应该返回401
    }

    @Test
    public void testRegister() {
        String registerJson = """
            {
                "username": "newuser",
                "email": "newuser@example.com",
                "password": "password123",
                "firstName": "New",
                "lastName": "User"
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(registerJson)
            .when().post("/api/auth/register")
            .then()
            .statusCode(201)
            .contentType(ContentType.JSON)
            .body("username", equalTo("newuser"))
            .body("email", equalTo("newuser@example.com"));
    }

    @Test
    public void testForgotPassword() {
        String forgotPasswordJson = """
            {
                "email": "test@example.com"
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(forgotPasswordJson)
            .when().post("/api/auth/forgot-password")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("message", containsString("重置邮件已发送"));
    }

    @Test
    public void testAdminListUsers() {
        given()
            .when().get("/api/admin/users")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON);
    }

    @Test
    public void testAdminCreateUser() {
        String createUserJson = """
            {
                "username": "adminuser",
                "email": "adminuser@example.com",
                "password": "password123",
                "firstName": "Admin",
                "lastName": "User"
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(createUserJson)
            .when().post("/api/admin/users")
            .then()
            .statusCode(201)
            .contentType(ContentType.JSON)
            .body("username", equalTo("adminuser"));
    }
} 