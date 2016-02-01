package com.edu.nc.bytesoft.ui.component;


import com.edu.nc.bytesoft.model.NamedEntity;
import com.edu.nc.bytesoft.model.User;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;

import java.util.StringJoiner;

public class UserAdditionalInfoForm  extends FormLayout{

    public UserAdditionalInfoForm(User user) {
        super();
        TextField nameField = new TextField("Username");
        TextField emailField = new TextField("Email");
        StringJoiner joiner = new StringJoiner(",");
        for (NamedEntity phone : user.getPhones()) {
            joiner.add(phone.getName());
        }
        TextField phonesField = new TextField("Phones");
        nameField.setValue(user.getUsername());
        emailField.setValue(user.getEmail());
        phonesField.setValue(joiner.toString());
        addComponents(nameField, emailField, phonesField);
    }
}

