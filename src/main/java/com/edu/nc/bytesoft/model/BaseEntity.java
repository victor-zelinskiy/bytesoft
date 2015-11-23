package com.edu.nc.bytesoft.model;

import java.util.Objects;

public abstract class BaseEntity {
    protected Long id;

    protected boolean isLazy;

    protected BaseEntity() {
    }

    protected BaseEntity(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isLazy() {
        return isLazy;
    }

    public void setLazy(boolean isLazy) {
        this.isLazy = isLazy;
    }

    public boolean isNew() {
        return (this.id == null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseEntity)) return false;
        BaseEntity that = (BaseEntity) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "\nid=" + id + " ";
    }

}

