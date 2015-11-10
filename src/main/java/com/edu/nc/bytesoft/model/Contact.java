package com.edu.nc.bytesoft.model;

import java.io.File;
import java.util.List;

public class Contact extends BaseEntity {
    protected String firstName;
    protected String lastName;
    protected String email;
    protected File photo;
    protected List<String> phones;

    public Contact() {
    }

    public Contact(Long id, String firstName, String lastName, String email, File photo, List<String> phones) {
        super(id);
        this.firstName = firstName;
        this.lastName = lastName;
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

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }


    @Override
    public String toString() {
        return "Contact{"
                + super.toString() +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", photo=" + photo +
                ", phones=" + phones +
                "} ";
    }
}
