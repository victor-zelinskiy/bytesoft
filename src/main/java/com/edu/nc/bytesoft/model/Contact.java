package com.edu.nc.bytesoft.model;

import com.edu.nc.bytesoft.dao.annotation.AttributeName;

import java.io.File;
import java.util.List;

public class Contact extends NamedEntity {
    @AttributeName("USR_EMAIL")
    protected String email;
    protected File photo;
    @AttributeName("USR_PHONE")
    protected List<String> phones;

    public Contact() {
    }

    public Contact(Long id, String name, String email, File photo, List<String> phones) {
        super(id, name);
        this.name = name;
        this.email = email;
        this.photo = photo;
        this.phones = phones;
    }


    public List<String> getPhones() {
        return phones;
    }

    public void setPhones(List<String> phones) {
        this.phones = phones;
    }

    public File getPhoto() {
        return photo;
    }

    public void setPhoto(File photo) {
        this.photo = photo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return "Contact{"
                + super.toString() +
                ", email='" + email + '\'' +
                ", photo=" + photo +
                ", phones=" + phones +
                "} ";
    }
}
