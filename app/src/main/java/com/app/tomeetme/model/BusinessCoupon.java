package com.app.tomeetme.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


@DatabaseTable(tableName = "Coupon")
public class BusinessCoupon {

    @DatabaseField(id = true)
    @SerializedName("id")
    @Expose
    private String id;

    @DatabaseField(canBeNull = true)
    @SerializedName("imageBase64")
    @Expose
    private String imageBase64;

    @DatabaseField(canBeNull = true)
    @SerializedName("imageFileName")
    @Expose
    private String imageFileName;

    @DatabaseField(canBeNull = true)
    @SerializedName("imagePath")
    @Expose
    private String imagePath;

    @DatabaseField(canBeNull = true)
    @SerializedName("businessId")
    @Expose
    private String businessId;

    @DatabaseField(canBeNull = true)
    @SerializedName("description")
    @Expose
    private String description;

    @DatabaseField(canBeNull = true)
    @SerializedName("placeOrBusiness")
    @Expose
    private String placeOrBusiness;

    @DatabaseField(canBeNull = true)
    @SerializedName("inPercent")
    @Expose
    private Boolean inPercent;

    @DatabaseField(canBeNull = true)
    @SerializedName("discount")
    @Expose
    private Double discount;

    @DatabaseField(canBeNull = true)
    @SerializedName("active")
    @Expose
    private Boolean active;

    @DatabaseField(canBeNull = true)
    @SerializedName("endDate")
    @Expose
    private String endDate;

    @DatabaseField(canBeNull = true)
    @SerializedName("startDate")
    @Expose
    private String startDate;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, foreignAutoCreate = true, index = true)
    private Business business;

    @DatabaseField(canBeNull = true)
    public Boolean favorite;

    public BusinessCoupon() {
    }

    public Business getBusiness() {
        return business;
    }

    public void setBusiness(Business business) {
        this.business = business;
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
     * @return The businessId
     */
    public String getBusinessId() {
        return businessId;
    }

    /**
     * @param businessId The businessId
     */
    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    /**
     * @return The description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return The placeOrBusiness
     */
    public String getPlaceOrBusiness() {
        return placeOrBusiness;
    }

    /**
     * @param placeOrBusiness The placeOrBusiness
     */
    public void setPlaceOrBusiness(String placeOrBusiness) {
        this.placeOrBusiness = placeOrBusiness;
    }

    /**
     * @return The inPercent
     */
    public Boolean getInPercent() {
        return inPercent;
    }

    /**
     * @param inPercent The inPercent
     */
    public void setInPercent(Boolean inPercent) {
        this.inPercent = inPercent;
    }

    /**
     * @return The discount
     */
    public Double getDiscount() {
        return discount;
    }

    /**
     * @param discount The discount
     */
    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    /**
     * @return The active
     */
    public Boolean getActive() {
        return active;
    }

    /**
     * @param active The active
     */
    public void setActive(Boolean active) {
        this.active = active;
    }

    /**
     * @return The endDate
     */
    public String getEndDate() {
        return endDate;
    }

    /**
     * @param endDate The endDate
     */
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    /**
     * @return The startDate
     */
    public String getStartDate() {
        return startDate;
    }

    /**
     * @param startDate The startDate
     */
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }


    public Boolean isFavorite() {
        return favorite == null ? false : favorite;
    }

    public void setFavorite(Boolean favorite) {
        this.favorite = favorite;
    }

}
