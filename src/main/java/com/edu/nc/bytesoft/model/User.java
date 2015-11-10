package com.edu.nc.bytesoft.model;

import java.util.Date;
import java.util.List;
import java.util.Set;

public class User extends BaseEntity {
    protected String login;
    protected String password;
    protected Date registered;
    protected String companyName;
    protected Set<Role> roles;
    protected List<Contact> contacts;

    public User() {
    }

    public User(Long id, String login, String password, Date registered, String companyName, List<Contact> contacts, Set<Role> roles) {
        super(id);
        this.login = login;
        this.password = password;
        this.registered = registered;
        this.companyName = companyName;
        this.contacts = contacts;
        this.roles = roles;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
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

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
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
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", registered=" + registered +
                ", companyName='" + companyName + '\'' +
                ", roles=" + roles +
                ", contacts=" + contacts +
                "} ";
    }
}
