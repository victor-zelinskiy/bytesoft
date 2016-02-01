package com.edu.nc.bytesoft.ui.component.validators;

public class PhoneValidator extends MyValidator {
    public PhoneValidator(String message) {
        super(message);
    }

    @Override
    public void validate(Object value) throws InvalidValueException {
        if (!isValid(value))
            throw new InvalidValueException("May consist of digits (at least 3)");
    }

    public boolean isValid(Object value) {
        String phoneNo = (String)value;
        if (phoneNo.matches("\\d{0,10}")) return true;
        else if(phoneNo.matches("\\d{3}[-\\.\\s]\\d{3}[-\\.\\s]\\d{4}")) return true;
        else if(phoneNo.matches("\\d{3}-\\d{3}-\\d{4}\\s(x|(ext))\\d{3,5}")) return true;
        else if(phoneNo.matches("\\(\\d{3}\\)-\\d{3}-\\d{4}")) return true;
        return false;
    }
}
