
package com.app.tomeetme.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "BusinessCategory")
public class BusinessCategory {
    @DatabaseField(id = true)
    private int id;
    @DatabaseField()
    private String description;
    @DatabaseField()
    private String Image;

    public BusinessCategory() {
    }

    /**
     * @return
     */
    public int getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }


    /**
     * @return
     */
    public String getImage() {
        return Image;
    }

    /**
     * @param image
     */
    public void setImage(String image) {
        Image = image;
    }
}
