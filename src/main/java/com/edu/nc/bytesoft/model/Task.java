package com.edu.nc.bytesoft.model;

import com.edu.nc.bytesoft.dao.annotation.AttributeName;
import com.edu.nc.bytesoft.dao.annotation.ObjTypeName;

import java.util.Date;
import java.util.List;

@ObjTypeName("TASK")
public class Task extends AbstractItem {
    @AttributeName("TSK_ASSIGN_USER")
    protected List<User> assignUser;

    @AttributeName("TSK_PRIORITY")
    protected Integer priority;

    @AttributeName("TSK_WATCHERS")
    protected List<User> watchers;

    @AttributeName("TSK_COMMENTS")
    protected List<Comment> comments;

    @AttributeName("TSK_PARENT_TASK")
    protected Task parentTask;

    public Task() {
    }

    public Task(Long id, String name, User author, Date createdDate, Status status, List<User> assignUser, Integer priority, List<User> watchers, List<Comment> comments, Task parentTask) {
        super(id, name, author, createdDate, status);
        this.assignUser = assignUser;
        this.priority = priority;
        this.watchers = watchers;
        this.comments = comments;
        this.parentTask = parentTask;
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
