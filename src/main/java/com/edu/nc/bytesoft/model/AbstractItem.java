package com.edu.nc.bytesoft.model;

import com.edu.nc.bytesoft.dao.annotation.AttributeName;

import java.util.Date;
import java.util.Objects;

public abstract class AbstractItem extends NamedEntity {

    @AttributeName("AIT_CREATOR")
    protected User author;
    @AttributeName("AIT_CREATE_DATE")
    protected Date createdDate;
    @AttributeName("AIT_DESCRIPTION")
    protected String description;
    @AttributeName("AIT_STATUS")
    protected Status status;
    @AttributeName("AIT_DEADLINE_DATE")
    protected Date deadlineDate;
    @AttributeName("AIT_COMPLETE_DATE")
    protected Date completedDate;

    public AbstractItem() {
    }

    public AbstractItem(String name, User author, Date createdDate, Status status) {
        super(name);
        this.author = author;
        this.createdDate = createdDate;
        this.status = status;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Date getDeadlineDate() {
        return deadlineDate;
    }

    public void setDeadlineDate(Date deadlineDate) {
        this.deadlineDate = deadlineDate;
    }

    public Date getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(Date completedDate) {
        this.completedDate = completedDate;
    }

    @Override
    public String toString() {
        return  super.toString() +
                "author=" + author +
                ", createdDate=" + createdDate +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", deadlineDate=" + deadlineDate +
                ", completedDate=" + completedDate;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractItem)) return false;
        if (!super.equals(o)) return false;
        AbstractItem that = (AbstractItem) o;
        return Objects.equals(getAuthor(), that.getAuthor()) &&
                Objects.equals(getCreatedDate(), that.getCreatedDate()) &&
                Objects.equals(getDescription(), that.getDescription()) &&
                getStatus() == that.getStatus() &&
                Objects.equals(getDeadlineDate(), that.getDeadlineDate()) &&
                Objects.equals(getCompletedDate(), that.getCompletedDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getAuthor(), getCreatedDate(), getDescription(), getStatus(), getDeadlineDate(), getCompletedDate());
    }
}
