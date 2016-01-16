package com.edu.nc.bytesoft.model;

import com.edu.nc.bytesoft.dao.annotation.AttributeName;
import com.edu.nc.bytesoft.dao.annotation.ObjTypeName;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@ObjTypeName(Project.TYPE_CODE)
public class Project extends AbstractItem {
    public static final String TYPE_CODE = "PROJECT";
    @AttributeName("PRJ_PRICE")
    protected Long price;

    @AttributeName("PRJ_DOCUMENTS")
    protected List<Document> documents = new ArrayList<>();

    @AttributeName("PRJ_MODULES")
    protected List<Module> modules = new ArrayList<>();

    @AttributeName("PRJ_ASSIGN_PMS")
    protected User assignProjectManagers = new User();

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public List<Module> getModules() {
        return modules;
    }

    public void setModules(List<Module> modules) {
        this.modules = modules;
    }

    public User getAssignProjectManagers() {
        return assignProjectManagers;
    }

    public void setAssignProjectManagers(User assignProjectManagers) {
        this.assignProjectManagers = assignProjectManagers;
    }
    @Override
    public String toString() {
        return "Project{" + super.toString() +
                "price=" + price +
                ", documents=" + documents +
                ", modules=" + modules +
                ", assignProjectManagers=" + assignProjectManagers +
                "} ";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Project)) return false;
        if (!super.equals(o)) return false;
        Project project = (Project) o;
        return Objects.equals(getPrice(), project.getPrice()) &&
                CollectionUtils.isEqualCollection(getDocuments(), project.getDocuments()) &&
                CollectionUtils.isEqualCollection(getModules(), project.getModules()) &&
                Objects.equals(getAssignProjectManagers(), project.getAssignProjectManagers());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getPrice(), getDocuments(), getModules(), getAssignProjectManagers());
    }
}
