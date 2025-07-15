package cn.ac.bestheme.oidc.earth.service;

import cn.ac.bestheme.oidc.earth.dto.*;
import cn.ac.bestheme.oidc.earth.util.CreatedResponseUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
public class KeycloakService {

    private static final Logger LOG = Logger.getLogger(KeycloakService.class);

    @ConfigProperty(name = "quarkus.keycloak.admin-client.server-url")
    String serverUrl;

    @ConfigProperty(name = "quarkus.keycloak.admin-client.realm")
    String adminRealm;

    @ConfigProperty(name = "quarkus.keycloak.admin-client.username")
    String adminUsername;

    @ConfigProperty(name = "quarkus.keycloak.admin-client.password")
    String adminPassword;

    @ConfigProperty(name = "quarkus.keycloak.admin-client.client-id")
    String adminClientId;

    @ConfigProperty(name = "quarkus.keycloak.devservices.realm-name")
    String realmName;

    private Keycloak keycloak;
    private RealmResource realmResource;

    public void init() {
        try {
            keycloak = KeycloakBuilder.builder()
                    .serverUrl(serverUrl)
                    .realm(adminRealm)
                    .username(adminUsername)
                    .password(adminPassword)
                    .clientId(adminClientId)
                    .build();

            realmResource = keycloak.realm(realmName);
            LOG.info("Keycloak service initialized successfully");
        } catch (Exception e) {
            LOG.error("Failed to initialize Keycloak service", e);
            throw new RuntimeException("Keycloak initialization failed", e);
        }
    }

    public UserDto createUser(UserCreateRequest request) {
        try {
            UsersResource usersResource = realmResource.users();
            
            UserRepresentation user = new UserRepresentation();
            user.setEnabled(true);
            user.setUsername(request.username());
            user.setEmail(request.email());
            user.setFirstName(request.firstName());
            user.setLastName(request.lastName());
            
            // 设置密码
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(request.password());
            credential.setTemporary(false);
            user.setCredentials(Arrays.asList(credential));

            Response response = usersResource.create(user);
            
            if (response.getStatus() == 201) {
                String userId = CreatedResponseUtil.getCreatedId(response);
                LOG.info("User created with ID: " + userId);
                
                // 获取创建的用户信息
                UserRepresentation createdUser = usersResource.get(userId).toRepresentation();
                return mapToUserDto(createdUser);
            } else {
                LOG.error("Failed to create user: " + response.getStatus());
                throw new WebApplicationException("Failed to create user", Response.Status.BAD_REQUEST);
            }
        } catch (Exception e) {
            LOG.error("Error creating user", e);
            throw new WebApplicationException("Error creating user", Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    public UserDto getUserById(String userId) {
        try {
            UsersResource usersResource = realmResource.users();
            UserRepresentation user = usersResource.get(userId).toRepresentation();
            return mapToUserDto(user);
        } catch (Exception e) {
            LOG.error("Error getting user by ID: " + userId, e);
            throw new WebApplicationException("User not found", Response.Status.NOT_FOUND);
        }
    }

    public UserDto getUserByUsername(String username) {
        try {
            UsersResource usersResource = realmResource.users();
            List<UserRepresentation> users = usersResource.search(username, true);
            
            if (users.isEmpty()) {
                throw new WebApplicationException("User not found", Response.Status.NOT_FOUND);
            }
            
            return mapToUserDto(users.get(0));
        } catch (WebApplicationException e) {
            throw e;
        } catch (Exception e) {
            LOG.error("Error getting user by username: " + username, e);
            throw new WebApplicationException("Error getting user", Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    public List<UserDto> getAllUsers() {
        try {
            UsersResource usersResource = realmResource.users();
            List<UserRepresentation> users = usersResource.list();
            
            return users.stream()
                    .map(this::mapToUserDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            LOG.error("Error getting all users", e);
            throw new WebApplicationException("Error getting users", Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    public UserDto updateUser(String userId, UserUpdateRequest request) {
        try {
            UsersResource usersResource = realmResource.users();
            UserResource userResource = usersResource.get(userId);
            UserRepresentation user = userResource.toRepresentation();
            
            if (request.username() != null) {
                user.setUsername(request.username());
            }
            if (request.email() != null) {
                user.setEmail(request.email());
            }
            if (request.firstName() != null) {
                user.setFirstName(request.firstName());
            }
            if (request.lastName() != null) {
                user.setLastName(request.lastName());
            }
            if (request.enabled() != null) {
                user.setEnabled(request.enabled());
            }
            
            userResource.update(user);
            
            return mapToUserDto(user);
        } catch (Exception e) {
            LOG.error("Error updating user: " + userId, e);
            throw new WebApplicationException("Error updating user", Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    public void deleteUser(String userId) {
        try {
            UsersResource usersResource = realmResource.users();
            usersResource.delete(userId);
            LOG.info("User deleted: " + userId);
        } catch (Exception e) {
            LOG.error("Error deleting user: " + userId, e);
            throw new WebApplicationException("Error deleting user", Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    public void assignRoles(String userId, List<String> roleNames) {
        try {
            UsersResource usersResource = realmResource.users();
            UserResource userResource = usersResource.get(userId);
            
            // 获取角色
            List<RoleRepresentation> roles = realmResource.roles().list();
            List<RoleRepresentation> userRoles = roles.stream()
                    .filter(role -> roleNames.contains(role.getName()))
                    .collect(Collectors.toList());
            
            userResource.roles().realmLevel().add(userRoles);
            LOG.info("Roles assigned to user: " + userId);
        } catch (Exception e) {
            LOG.error("Error assigning roles to user: " + userId, e);
            throw new WebApplicationException("Error assigning roles", Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    public void changePassword(String userId, String newPassword) {
        try {
            UsersResource usersResource = realmResource.users();
            UserResource userResource = usersResource.get(userId);
            
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(newPassword);
            credential.setTemporary(false);
            
            userResource.resetPassword(credential);
            LOG.info("Password changed for user: " + userId);
        } catch (Exception e) {
            LOG.error("Error changing password for user: " + userId, e);
            throw new WebApplicationException("Error changing password", Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    public AuthResponse login(LoginRequest request) {
        try {
            // 使用Keycloak Admin Client进行登录验证
            Keycloak userKeycloak = KeycloakBuilder.builder()
                    .serverUrl(serverUrl)
                    .realm(realmName)
                    .username(request.username())
                    .password(request.password())
                    .clientId("earth-client")
                    .build();

            // 获取访问令牌
            String accessToken = userKeycloak.tokenManager().getAccessToken().getToken();
            
            // 获取用户信息
            UserDto user = getUserByUsername(request.username());
            
            return new AuthResponse(accessToken, user);
        } catch (Exception e) {
            LOG.error("Login failed for user: " + request.username(), e);
            throw new WebApplicationException("Invalid credentials", Response.Status.UNAUTHORIZED);
        }
    }

    private UserDto mapToUserDto(UserRepresentation user) {
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.isEnabled(),
                user.getCreatedTimestamp(),
                user.getAttributes() != null ? user.getAttributes() : new HashMap<>()
        );
    }

    public void close() {
        if (keycloak != null) {
            keycloak.close();
        }
    }
} 