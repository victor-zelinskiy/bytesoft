package com.edu.nc.bytesoft.model;

import com.edu.nc.bytesoft.dao.annotation.AttributeName;
import com.edu.nc.bytesoft.dao.annotation.ObjTypeName;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@ObjTypeName(User.TYPE_CODE)
public class User extends Contact implements UserDetails {
    public static final String TYPE_CODE = "USER";
    @AttributeName("USR_LOGIN")
    protected String username;

    @AttributeName("USR_PASSWORD")
    protected String password;

    @AttributeName("USR_REG_DATE")
    protected Date registered;

    @AttributeName("USR_COMPANY_NAME")
    protected String companyName;

    @AttributeName("USR_ROLES")
    protected List<Role> roles = new ArrayList<>();

    @AttributeName("USR_CONTACTS")
    protected List<Contact> contacts = new ArrayList<>();

    public User() {
    }


    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getRegistered() {
        return registered;
    }

    public void setRegistered(Date registered) {
        this.registered = registered;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    @Override
    public String toString() {
        return "User{" + super.toString() +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", registered=" + registered +
                ", companyName='" + companyName + '\'' +
                ", roles=" + roles +
                ", contacts=" + contacts +
                "} ";
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        if (!super.equals(o)) return false;
        User user = (User) o;
        return Objects.equals(getUsername(), user.getUsername()) &&
                Objects.equals(getPassword(), user.getPassword()) &&
                Objects.equals(getRegistered(), user.getRegistered()) &&
                Objects.equals(getCompanyName(), user.getCompanyName()) &&
                CollectionUtils.isEqualCollection(getRoles(), user.getRoles()) &&
                CollectionUtils.isEqualCollection(getContacts(), user.getContacts());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getUsername(), getPassword(), getRegistered(), getCompanyName(), getRoles(), getContacts());
    }
}
