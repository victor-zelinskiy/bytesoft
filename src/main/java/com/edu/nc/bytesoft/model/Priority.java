package com.edu.nc.bytesoft.model;


public enum Priority implements IdentifiableEnum {
    CRITICAL(5),
    HIGH(4),
    MEDIUM(3),
    LOW(2),
    FUTURE(1);
    private long id;

    Priority(long id) {
        this.id = id;
    }

    public static  Priority getById(Long id) {

        if (id == null) {
            return null;
        }

        for ( Priority priority :  Priority.values()) {
            if (id.equals(priority.getId())) return priority;
        }
        throw new IllegalArgumentException("No matching Priority for id " + id);
    }
    public long getId() {
        return id;
    }
}
