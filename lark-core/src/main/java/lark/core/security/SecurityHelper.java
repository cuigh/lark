package lark.core.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Security helper class
 *
 * @author cuigh
 */
public final class SecurityHelper {
    private SecurityHelper() {
        // Prevent to create instance
    }

    @SuppressWarnings("unchecked")
    public static <T extends UserDetails> T getUser() {
        return (T) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
