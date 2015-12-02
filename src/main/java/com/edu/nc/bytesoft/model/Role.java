package com.edu.nc.bytesoft.model;


import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority, IdentifiableEnum {
    ROLE_CUSTOMER(2),
    ROLE_ADMIN(3),
    ROLE_DEVELOPER(4),
    ROLE_TEAM_LEAD(5),
    ROLE_PROJECT_MANAGER(6);

    private long id;

    Role(long id) {
        this.id = id;
    }

    public static Role getById(Long id) {

        if (id == null) {
            return null;
        }

        for (Role role : Role.values()) {
            if (id.equals(role.getId())) {
                return role;
            }
        }
        throw new IllegalArgumentException("No matching Role for id " + id);
    }

    public long getId() {
        return id;
    }

    @Override
    public String getAuthority() {
        return name();
    }
}
