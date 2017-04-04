package com.app.tomeetme.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


@DatabaseTable(tableName = "BusinessPictures")
public class BusinessPictures {

    @DatabaseField()
    @SerializedName("CarId")
    @Expose
    private String carId;

    @DatabaseField()
    @SerializedName("imagePath")
    @Expose
    private String imagePath;

    @DatabaseField()
    @SerializedName("imageBase64")
    @Expose
    private String imageBase64;

    @DatabaseField()
    @SerializedName("imageFileName")
    @Expose
    private String imageFileName;

    @DatabaseField(id = true)
    @SerializedName("id")
    @Expose
    private String id;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, foreignAutoCreate = true, index = true)
    private Business business;


    public BusinessPictures() {
    }

    public Business getBusiness() {
        return business;
    }

    public void setBusiness(Business business) {
        this.business = business;
    }

    /**
     * @return The carId
     */
    public String getBusinessId() {
        return carId;
    }

    /**
     * @param carId The carId
     */
    public void setBusinessId(String carId) {
        this.carId = carId;
    }

    /**
     * @return The imagePath
     */
    public String getImagePath() {
        return imagePath;
    }

    /**
     * @param imagePath The imagePath
     */
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    /**
     * @return The imageBase64
     */
    public String getImageBase64() {
        return imageBase64;
    }

    /**
     * @param imageBase64 The imageBase64
     */
    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    /**
     * @return The imageFileName
     */
    public String getImageFileName() {
        return imageFileName;
    }

    /**
     * @param imageFileName The imageFileName
     */
    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    /**
     * @return The id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(String id) {
        this.id = id;
    }

}