package com.scnsoft.exchange.audit.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class AuditUser extends User {
    private Long id;
    private Long companyId;

    public AuditUser(Long id, String username, Long companyId, String password,
                     Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.companyId = companyId;
        this.id = id;
    }

    public AuditUser(Long id, String username, Long companyId, String password,
                     boolean enabled,
                     boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked,
                     Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.companyId = companyId;
        this.id = id;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public Long getId() {
        return id;
    }
}
