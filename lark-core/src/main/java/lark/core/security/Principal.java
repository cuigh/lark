package lark.core.security;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author cuigh
 */
public interface Principal extends UserDetails {
    boolean hasPermission(String resource, String action, String id);
}
