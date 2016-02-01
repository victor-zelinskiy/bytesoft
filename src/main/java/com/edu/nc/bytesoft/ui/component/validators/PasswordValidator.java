package com.edu.nc.bytesoft.ui.component.validators;

public class PasswordValidator extends MyValidator {
    public PasswordValidator(String message) {
        super(message);
    }

    @Override
    public void validate(Object value) throws InvalidValueException {
        if (!isValid(value))
            throw new InvalidValueException("Must contain at least one lower case letter, one upper case letter, one digit. No spaces. Minimal length - 6");
    }

    @Override
    public boolean isValid(Object value) {
        String pass = (String)value;
        if(pass.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{6,}$"))
            return true;
        return false;
    }
}
