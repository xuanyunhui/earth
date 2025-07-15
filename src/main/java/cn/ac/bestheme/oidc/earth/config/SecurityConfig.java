package cn.ac.bestheme.oidc.earth.config;

import io.quarkus.security.runtime.SecurityIdentityProxy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 安全配置类
 * 定义权限映射和角色配置
 */
@ApplicationScoped
public class SecurityConfig {

    private static final Logger LOG = Logger.getLogger(SecurityConfig.class);

    @ConfigProperty(name = "app.security.enabled", defaultValue = "true")
    boolean securityEnabled;

    // 权限映射：权限名称 -> 所需角色
    private static final Map<String, Set<String>> PERMISSION_ROLE_MAP = new HashMap<>();

    static {
        // 管理员权限
        PERMISSION_ROLE_MAP.put("admin", Set.of("admin"));
        PERMISSION_ROLE_MAP.put("admin:read", Set.of("admin"));
        PERMISSION_ROLE_MAP.put("admin:create", Set.of("admin"));
        PERMISSION_ROLE_MAP.put("admin:update", Set.of("admin"));
        PERMISSION_ROLE_MAP.put("admin:delete", Set.of("admin"));
        PERMISSION_ROLE_MAP.put("admin:manage-roles", Set.of("admin"));

        // 用户权限
        PERMISSION_ROLE_MAP.put("user", Set.of("user", "admin"));
        PERMISSION_ROLE_MAP.put("user:read", Set.of("user", "admin"));
        PERMISSION_ROLE_MAP.put("user:update", Set.of("user", "admin"));
        PERMISSION_ROLE_MAP.put("user:change-password", Set.of("user", "admin"));
    }

    /**
     * 检查用户是否具有指定权限
     * @param permission 权限名称
     * @param userRoles 用户角色集合
     * @return 是否具有权限
     */
    public boolean hasPermission(String permission, Set<String> userRoles) {
        if (!securityEnabled) {
            LOG.debug("Security disabled, allowing all permissions");
            return true;
        }

        Set<String> requiredRoles = PERMISSION_ROLE_MAP.get(permission);
        if (requiredRoles == null) {
            LOG.warn("Unknown permission: " + permission);
            return false;
        }

        // 检查用户角色是否包含所需角色
        boolean hasPermission = userRoles.stream().anyMatch(requiredRoles::contains);
        LOG.debug("Permission check: " + permission + " for roles " + userRoles + " = " + hasPermission);
        
        return hasPermission;
    }

    /**
     * 检查用户是否具有指定角色
     * @param requiredRole 所需角色
     * @param userRoles 用户角色集合
     * @return 是否具有角色
     */
    public boolean hasRole(String requiredRole, Set<String> userRoles) {
        if (!securityEnabled) {
            LOG.debug("Security disabled, allowing all roles");
            return true;
        }

        boolean hasRole = userRoles.contains(requiredRole);
        LOG.debug("Role check: " + requiredRole + " for roles " + userRoles + " = " + hasRole);
        
        return hasRole;
    }

    /**
     * 获取所有权限列表
     * @return 权限映射
     */
    public Map<String, Set<String>> getAllPermissions() {
        return new HashMap<>(PERMISSION_ROLE_MAP);
    }

    /**
     * 获取权限所需的角色
     * @param permission 权限名称
     * @return 所需角色集合
     */
    public Set<String> getRequiredRoles(String permission) {
        return PERMISSION_ROLE_MAP.get(permission);
    }
} 