package com.attendance.model;

import java.sql.Timestamp;

public class AcademicYear {
    private int id;
    private String yearLabel;
    private int sortOrder;
    private boolean active;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public AcademicYear() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getYearLabel() { return yearLabel; }
    public void setYearLabel(String yearLabel) { this.yearLabel = yearLabel; }

    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}
