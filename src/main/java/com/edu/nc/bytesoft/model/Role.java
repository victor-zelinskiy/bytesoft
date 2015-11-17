package com.edu.nc.bytesoft.model;


import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ROLE_ADMIN,
    ROLE_CUSTOMER,
    ROLE_DEVELOPER,
    ROLE_TEAM_LEAD,
    ROLE_PROJECT_MANAGER;

    @Override
    public String getAuthority() {
        return name();
    }
}
