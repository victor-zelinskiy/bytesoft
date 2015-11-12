package com.edu.nc.bytesoft.model;

import java.util.List;

public class Task extends AbstractItem {
    protected List<User> assignUser;
    protected Integer priority;
    protected List<User> watchers;
    protected List<Comment> comments;
    protected Task parentTask;

    public Task() {
    }

    public List<User> getAssignUser() {
        return assignUser;
    }

    public void setAssignUser(List<User> assignUser) {
        this.assignUser = assignUser;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public List<User> getWatchers() {
        return watchers;
    }

    public void setWatchers(List<User> watchers) {
        this.watchers = watchers;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public Task getParentTask() {
        return parentTask;
    }

    public void setParentTask(Task parentTask) {
        this.parentTask = parentTask;
    }

    @Override
    public String toString() {
        return "Task{" + super.toString() +
                "assignUser=" + assignUser +
                ", priority=" + priority +
                ", watchers=" + watchers +
                ", comments=" + comments +
                ", parentTask=" + parentTask +
                "} ";
    }
}
