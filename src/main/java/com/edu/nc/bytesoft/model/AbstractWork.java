package com.edu.nc.bytesoft.model;

import java.util.Date;

public abstract class AbstractWork extends NamedEntity {
    protected User author;
    protected Date createdDate;
    protected String description;
    protected Status status;
    protected Date deadlineDate;
    protected Date completedDate;

    public AbstractWork() {
    }

    public AbstractWork(Long id, String name, User author, Date createdDate, Status status) {
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
