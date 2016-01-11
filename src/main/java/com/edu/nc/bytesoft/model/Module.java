package com.edu.nc.bytesoft.model;

import com.edu.nc.bytesoft.dao.annotation.AttributeName;
import com.edu.nc.bytesoft.dao.annotation.ObjTypeName;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@ObjTypeName(Module.TYPE_CODE)
public class Module extends AbstractItem {
    public static final String TYPE_CODE = "MODULE";
    @AttributeName("MDL_ASSIGN_TM")
    protected List<User> assignTeamLeads = new ArrayList<>();

    @AttributeName("MDL_PRIORITY")
    protected Priority priority;

    @AttributeName("MDL_TASKS")
    protected List<Task> tasks = new ArrayList<>();

    public List<User> getAssignTeamLeads() {
        return assignTeamLeads;
    }

    public void setAssignTeamLeads(List<User> assignTeamLeads) {
        this.assignTeamLeads = assignTeamLeads;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    @Override
    public String toString() {
        return "Module{" + super.toString() +
                "assignTeamLeads=" + assignTeamLeads +
                ", priority=" + priority +
                ", tasks=" + tasks +
                "} ";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Module)) return false;
        if (!super.equals(o)) return false;
        Module module = (Module) o;
        return Objects.equals(getPriority(), module.getPriority()) &&
                CollectionUtils.isEqualCollection(getAssignTeamLeads(), module.getAssignTeamLeads()) &&
                CollectionUtils.isEqualCollection(getTasks(), module.getTasks());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getAssignTeamLeads(), getPriority(), getTasks());
    }
}
