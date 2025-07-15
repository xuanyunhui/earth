package cn.ac.bestheme.oidc.earth;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

@QuarkusTest
public class UserResourceTest {

    @Test
    public void testGetAllUsers() {
        given()
            .when().get("/api/users")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body(is("[]"));
    }

    @Test
    public void testCreateUser() {
        String userJson = """
            {
                "username": "testuser",
                "email": "test@example.com",
                "fullName": "Test User"
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(userJson)
            .when().post("/api/users")
            .then()
            .statusCode(201)
            .contentType(ContentType.JSON)
            .body("username", equalTo("testuser"))
            .body("email", equalTo("test@example.com"))
            .body("fullName", equalTo("Test User"))
            .body("status", equalTo("ACTIVE"));
    }

    @Test
    public void testGetUserById() {
        // 先创建用户
        String userJson = """
            {
                "username": "testuser2",
                "email": "test2@example.com",
                "fullName": "Test User 2"
            }
            """;

        String location = given()
            .contentType(ContentType.JSON)
            .body(userJson)
            .when().post("/api/users")
            .then()
            .statusCode(201)
            .extract().header("Location");

        // 获取用户ID
        String userId = location.substring(location.lastIndexOf("/") + 1);

        // 根据ID获取用户
        given()
            .when().get("/api/users/" + userId)
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("username", equalTo("testuser2"));
    }

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
} 