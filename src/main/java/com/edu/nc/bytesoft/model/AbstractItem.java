package com.edu.nc.bytesoft.model;

import com.edu.nc.bytesoft.dao.annotation.AttributeName;

import java.util.Date;

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

    public AbstractItem(Long id, String name, User author, Date createdDate, Status status) {
        super(id, name);
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
}
