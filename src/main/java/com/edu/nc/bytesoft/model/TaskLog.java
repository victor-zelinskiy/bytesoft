package com.edu.nc.bytesoft.model;

import java.util.Date;
import java.util.Objects;

public class TaskLog extends BaseEntity {
    protected User changer;
    protected Date changeDate;
    protected Object oldValue;
    protected Object newValue;
    protected TaskChangeType changeType;

    public TaskLog() {
    }

    public TaskLog(Long id, User changer, Date changeDate, Object oldValue, Object newValue, TaskChangeType changeType) {
        super(id);
        this.changer = changer;
        this.changeDate = changeDate;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.changeType = changeType;
    }

    public User getChanger() {
        return changer;
    }

    public void setChanger(User changer) {
        this.changer = changer;
    }

    public Date getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(Date changeDate) {
        this.changeDate = changeDate;
    }

    public Object getOldValue() {
        return oldValue;
    }

    public void setOldValue(Object oldValue) {
        this.oldValue = oldValue;
    }

    public Object getNewValue() {
        return newValue;
    }

    public void setNewValue(Object newValue) {
        this.newValue = newValue;
    }

    public TaskChangeType getChangeType() {
        return changeType;
    }

    public void setChangeType(TaskChangeType changeType) {
        this.changeType = changeType;
    }

    @Override
    public String toString() {
        return "TaskLog{" + super.toString() +
                ", changer=" + changer +
                ", changeDate=" + changeDate +
                ", oldValue=" + oldValue +
                ", newValue=" + newValue +
                ", changeType=" + changeType +
                "} ";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaskLog)) return false;
        if (!super.equals(o)) return false;
        TaskLog taskLog = (TaskLog) o;
        return Objects.equals(getChanger(), taskLog.getChanger()) &&
                Objects.equals(getChangeDate(), taskLog.getChangeDate()) &&
                Objects.equals(getOldValue(), taskLog.getOldValue()) &&
                Objects.equals(getNewValue(), taskLog.getNewValue()) &&
                getChangeType() == taskLog.getChangeType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getChanger(), getChangeDate(), getOldValue(), getNewValue(), getChangeType());
    }
}
