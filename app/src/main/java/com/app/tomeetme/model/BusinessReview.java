package com.app.tomeetme.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "BusinessReview")
public class BusinessReview {

    @DatabaseField(id = true)
    @SerializedName("id")
    @Expose
    private String id;

    @DatabaseField(canBeNull = true)
    @SerializedName("lastName")
    @Expose
    private String lastName;

    @DatabaseField(canBeNull = true)
    @SerializedName("firstName")
    @Expose
    private String firstName;

    @DatabaseField(canBeNull = true)
    @SerializedName("email")
    @Expose
    private String email;

    @DatabaseField(canBeNull = true)
    @SerializedName("businessId")
    @Expose
    private String businessId;

    @DatabaseField(canBeNull = true)
    @SerializedName("stars")
    @Expose
    private Integer stars;

    @DatabaseField(canBeNull = true)
    @SerializedName("comment")
    @Expose
    private String comment;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, foreignAutoCreate = true, index = true)
    private Business business;

    @DatabaseField(canBeNull = true)
    @SerializedName("date")
    @Expose
    private String date;

    public BusinessReview() {
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
     * @return The lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName The lastName
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return The firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName The firstName
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return The email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email The email
     */
    public void setEmail(String email) {
        this.email = email;
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
     * @return The stars
     */
    public Integer getStars() {
        return stars;
    }

    /**
     * @param stars The stars
     */
    public void setStars(Integer stars) {
        this.stars = stars;
    }

    /**
     * @return The comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * @param comment The comment
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
