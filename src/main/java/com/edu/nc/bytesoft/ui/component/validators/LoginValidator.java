package com.edu.nc.bytesoft.ui.component.validators;

public class LoginValidator extends MyValidator {
    public LoginValidator(String message) {
        super(message);
    }

    @Override
    public void validate(Object value) throws InvalidValueException {
        if (!isValid(value))
            throw new InvalidValueException("May consist of latin letters, digits, spaces and underlines");
    }

    public boolean isValid(Object value) {
        String login = (String)value;
        if(login.matches("^[A-z0-9_-]{3,15}$")) return true;
        return false;
    }
}
