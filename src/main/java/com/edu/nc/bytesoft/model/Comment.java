package com.edu.nc.bytesoft.model;

import com.edu.nc.bytesoft.dao.annotation.AttributeName;
import com.edu.nc.bytesoft.dao.annotation.ObjTypeName;

import java.util.Date;
import java.util.Objects;

@ObjTypeName(Comment.TYPE_CODE)
public class Comment extends BaseEntity {
    public static final String TYPE_CODE = "COMMENT_ITEM";
    @AttributeName("ICM_AUTHOR")
    protected User author;
    @AttributeName("ICM_ADD_DATE")
    protected Date addedDate;
    @AttributeName("ICM_MESSAGE")
    protected String message;
    @AttributeName("ICM_PARENT_COMMENT")
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Comment)) return false;
        if (!super.equals(o)) return false;
        Comment comment = (Comment) o;
        return Objects.equals(getAuthor(), comment.getAuthor()) &&
                Objects.equals(getAddedDate(), comment.getAddedDate()) &&
                Objects.equals(getMessage(), comment.getMessage()) &&
                Objects.equals(getParent(), comment.getParent());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getAuthor(), getAddedDate(), getMessage(), getParent());
    }
}
