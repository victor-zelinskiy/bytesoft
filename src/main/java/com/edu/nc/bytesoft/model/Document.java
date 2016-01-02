package com.edu.nc.bytesoft.model;

import java.io.File;
import java.util.Date;
import java.util.Objects;

public class Document extends NamedEntity {
    protected Date addedDate;
    protected String description;
    protected DocumentType type;
    protected File file;

    public Document() {
    }

    public Document(Long id, String name, Date addedDate, String description, DocumentType type, File file) {
        super(id, name);
        this.addedDate = addedDate;
        this.description = description;
        this.type = type;
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Date getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(Date addedDate) {
        this.addedDate = addedDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DocumentType getType() {
        return type;
    }

    public void setType(DocumentType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Document{" + super.toString() +
                ", addedDate=" + addedDate +
                ", description='" + description + '\'' +
                ", type=" + type +
                ", file=" + file +
                "} ";
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Document)) return false;
        if (!super.equals(o)) return false;
        Document document = (Document) o;
        return Objects.equals(getAddedDate(), document.getAddedDate()) &&
                Objects.equals(getDescription(), document.getDescription()) &&
                getType() == document.getType() &&
                Objects.equals(getFile(), document.getFile());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getAddedDate(), getDescription(), getType(), getFile());
    }
}
