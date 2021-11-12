package lark.core.security;

import lark.core.util.Exceptions;
import lark.core.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.util.Collection;

/**
 * @author cuigh
 */
@Permission(type = "t", id = "s")
public class PermissionChecker implements PermissionEvaluator {
    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if ((authentication == null) || (targetDomainObject == null) || !(permission instanceof String)) {
            return false;
        }

        // hasPermission('user', 'delete')
        if (targetDomainObject instanceof String) {
            return hasPermission(authentication, (String) targetDomainObject, permission.toString(), null);
        }

        // hasPermission(user, 'delete')
        Class<?> cls = targetDomainObject.getClass();
        Permission perm = cls.getAnnotation(Permission.class);
        PropertyDescriptor descriptor = BeanUtils.getPropertyDescriptor(cls, perm.id());
        if (descriptor == null) {
            throw new RuntimeException(String.format("Class '%s' doesn't have '%s' property", cls, perm.id()));
        }

        try {
            Object result = descriptor.getReadMethod().invoke(targetDomainObject);
            return hasPermission(authentication, perm.type(), permission.toString(), result.toString());
        } catch (Exception e) {
            throw Exceptions.asRuntime(e);
        }
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        // hasPermission(123, 'movie', 'delete')
        if ((authentication == null) || (targetId == null) || Strings.isEmpty(targetType) || !(permission instanceof String)) {
            return false;
        }

        return hasPermission(authentication, targetType, permission.toString(), targetId.toString());
    }

    private boolean hasPermission(Authentication authentication, String resource, String action, String id) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof Principal) {
            return ((Principal) principal).hasPermission(resource, action, id);
        } else {
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            for (GrantedAuthority authority : authorities) {
                if (isMatch(authority.getAuthority(), resource, action, id)) {
                    return true;
                }
            }
            return false;
        }
    }

    private boolean isMatch(String authority, String... perms) {
        // authority: [movie, delete, 123], [movie, delete], [movie, delete, *], [movie, *, 123]
        // perms: [movie, delete, 123]
        String[] parts = authority.split(":");
        if (parts.length > perms.length) {
            return false;
        }
        for (int i = 0; i < parts.length; i++) {
            if (!parts[i].equals(perms[i]) && !"*".equals(parts[i])) {
                return false;
            }
        }
        return true;
    }
}
