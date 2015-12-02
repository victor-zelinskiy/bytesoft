package com.edu.nc.bytesoft.model;

public enum Status implements IdentifiableEnum {
    STATUS_ACTIVE(13),
    STATUS_RESOLVED(14),
    STATUS_SUSPENDED(15),
    STATUS_CLOSED(16);

    private long id;

    Status(long id) {
        this.id = id;
    }

    public static Status getById(Long id) {

        if (id == null) {
            return null;
        }

        for (Status status : Status.values()) {
            if (id.equals(status.getId())) {
                return status;
            }
        }
        throw new IllegalArgumentException("No matching Status for id " + id);
    }

    public long getId() {
        return id;
    }
}
