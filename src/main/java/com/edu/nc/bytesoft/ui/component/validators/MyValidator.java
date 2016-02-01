package com.edu.nc.bytesoft.ui.component.validators;

import com.vaadin.data.Validator;

public abstract class MyValidator implements Validator {
    private String message;

    public abstract boolean isValid(Object value);

    public MyValidator(String message) {
        this.message = message;
    }
}
