package com.app.tomeetme.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "BusinessAddress")
public class BusinessAddress extends AbstractMapModel implements Comparable<BusinessAddress>{

    @DatabaseField(id = true)
    @SerializedName("id")
    @Expose
    private int id;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, foreignAutoCreate = true, index = true)
    private Business business;

    @DatabaseField
    @SerializedName("address")
    @Expose
    private String address;

    @DatabaseField
    private float distance;

    @DatabaseField
    private float lastknowpeoplecount;

    public BusinessAddress() {
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
    public int getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(int id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    @Override
    public int compareTo(BusinessAddress model) {
        if (this.getDistance() < model.getDistance()) return -1;
        else if (this.getDistance() > model.getDistance()) return 1;
        else return 0;
    }

    @Override
    public LatLng getPosition() {
        return new LatLng(Double.parseDouble(getLatitude()), Double.parseDouble(getLongitude()));
    }

    public String getLongitude() {
        return longitude;
    }

    /**
     * @param longitude The longitude
     */
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    /**
     * @return The latitude
     */
    public String getLatitude() {
        return latitude;
    }

    /**
     * @param latitude The latitude
     */
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    @Override
    public Boolean isFavorite() {
        return favorite == null ? false : favorite;
    }

    @Override
    public void setFavorite(Boolean favorite) {
        this.favorite = favorite;
    }

    public float getLastknowpeoplecount() {
        return lastknowpeoplecount;
    }

    public void setLastknowpeoplecount(float lastknowpeoplecount) {
        this.lastknowpeoplecount = lastknowpeoplecount;
    }
}
