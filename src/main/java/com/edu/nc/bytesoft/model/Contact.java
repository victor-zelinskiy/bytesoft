package com.edu.nc.bytesoft.model;

import com.edu.nc.bytesoft.dao.annotation.AttributeName;
import org.apache.commons.collections.CollectionUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Contact extends NamedEntity {
    @AttributeName("USR_EMAIL")
    protected String email;
    protected File photo;
    @AttributeName("USR_PHONE")
    protected List<NamedEntity> phones = new ArrayList<>();

    public Contact() {
    }

    public Contact(Long id, String name, String email, File photo, List<NamedEntity> phones) {
        super(id, name);
        this.name = name;
        this.email = email;
        this.photo = photo;
        this.phones = phones;
    }


    public List<NamedEntity> getPhones() {
        return phones;
    }

    public void setPhones(List<NamedEntity> phones) {
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Contact)) return false;
        if (!super.equals(o)) return false;
        Contact contact = (Contact) o;
        return Objects.equals(getEmail(), contact.getEmail()) &&
                Objects.equals(getPhoto(), contact.getPhoto()) &&
                CollectionUtils.isEqualCollection(getPhones(), contact.getPhones());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getEmail(), getPhoto(), getPhones());
    }
}
