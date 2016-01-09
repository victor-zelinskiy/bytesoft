package com.edu.nc.bytesoft.service.exception;


public class NotUniqueEmailException extends Exception {

    public NotUniqueEmailException() {
        super();
    }

    public NotUniqueEmailException(String message) {
        super(message);
    }

    public NotUniqueEmailException(Throwable cause) {
        super(cause);
    }

    public NotUniqueEmailException(String reason, Throwable cause) {
        super(reason, cause);
    }
}

