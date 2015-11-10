package com.edu.nc.bytesoft.model;

import java.util.Date;

public class Comment extends BaseEntity{
    protected User author;
    protected Date addedDate;
    protected String message;
    protected Comment parent;

    public Comment() {
    }

    public Comment(Long id, User author, Date addedDate, String message, Comment parent) {
        super(id);
        this.author = author;
        this.addedDate = addedDate;
        this.message = message;
        this.parent = parent;
    }

    public Comment(Long id, User author, Date addedDate, String message) {
        super(id);
        this.author = author;
        this.addedDate = addedDate;
        this.message = message;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Date getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(Date addedDate) {
        this.addedDate = addedDate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Comment getParent() {
        return parent;
    }

    public void setParent(Comment parent) {
        this.parent = parent;
    }

    @Override
    public String toString() {
        return "Comment{" + super.toString() +
                ", author=" + author +
                ", addedDate=" + addedDate +
                ", message='" + message + '\'' +
                ", parent=" + parent +
                "} ";
    }
}
