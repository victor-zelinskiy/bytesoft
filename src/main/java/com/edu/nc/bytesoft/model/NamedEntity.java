package com.edu.nc.bytesoft.model;

import com.edu.nc.bytesoft.dao.annotation.AttributeName;

public class NamedEntity extends BaseEntity {
    @AttributeName({"AIT_NAME", "USR_NAME", "ICN_NAME", "IPN_VALUE"})
    protected String name;

    public NamedEntity() {
    }

    protected NamedEntity(Long id, String name) {
        super(id);
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return super.toString() + ", name='" + name + '\'';
    }
}
