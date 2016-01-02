package com.edu.nc.bytesoft.dao.exception;


public class NoSuchObjectException extends Exception {

    public NoSuchObjectException() {
        super();
    }

    public NoSuchObjectException(String message) {
        super(message);
    }

    public NoSuchObjectException(Throwable cause) {
        super(cause);
    }

    public NoSuchObjectException(String reason, Throwable cause) {
        super(reason, cause);
    }
}
