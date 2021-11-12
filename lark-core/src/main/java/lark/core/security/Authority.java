package lark.core.security;

import org.springframework.security.core.GrantedAuthority;

/**
 * A {@link GrantedAuthority} implementation based on resource and action model.
 *
 * @author cuigh
 */
public class Authority implements GrantedAuthority {
    private String perm;

    public Authority(String resource, String action) {
        this.perm = resource + ":" + action;
    }

    @Override
    public String getAuthority() {
        return perm;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof Authority) {
            return perm.equals(((Authority) obj).perm);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return this.perm.hashCode();
    }

    @Override
    public String toString() {
        return this.perm;
    }
}
