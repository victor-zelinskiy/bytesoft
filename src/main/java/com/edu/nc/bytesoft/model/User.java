package com.edu.nc.bytesoft.model;

import com.edu.nc.bytesoft.dao.annotation.AttributeName;

import java.util.Date;
import java.util.List;

public class User extends Contact {
    @AttributeName("USR_LOGIN")
    protected String login;

    @AttributeName("USR_PASSWORD")
    protected String password;

    @AttributeName("USR_REG_DATE")
    protected Date registered;

    @AttributeName("USR_COMPANY_NAME")
    protected String companyName;

    @AttributeName("USR_ROLES")
    protected List<Role> roles;

    @AttributeName("USR_CONTACTS")
    protected List<Contact> contacts;

    public User() {
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
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", registered=" + registered +
                ", companyName='" + companyName + '\'' +
                ", roles=" + roles +
                ", contacts=" + contacts +
                "} ";
    }
}
