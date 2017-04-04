
package com.app.tomeetme.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.joda.time.DateTime;

@DatabaseTable(tableName = "BusinessDaysSchedule")
public class BusinessDaysSchedule {
    @DatabaseField(id = true)
    private int id;
    @DatabaseField()
    private String description;
    @DatabaseField()
    private DateTime start;
    @DatabaseField()
    private DateTime end;
    @DatabaseField()
    private Boolean enable;

    public BusinessDaysSchedule() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DateTime getStart() {
        return start;
    }

    public void setStart(DateTime start) {
        this.start = start;
    }

    public DateTime getEnd() {
        return end;
    }

    public void setEnd(DateTime end) {
        this.end = end;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }
}
