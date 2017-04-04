package com.app.tomeetme.model;


import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.maps.android.clustering.ClusterItem;
import com.j256.ormlite.field.DatabaseField;

public abstract class AbstractMapModel implements ClusterItem {

    @DatabaseField(canBeNull = true)
    @SerializedName("longitude")
    @Expose
    public String longitude;

    @DatabaseField(canBeNull = true)
    @SerializedName("latitude")
    @Expose
    public String latitude;

    @DatabaseField(canBeNull = true)
    public Boolean favorite;

    public abstract String getLongitude();

    public abstract void setLongitude(String longitude);

    public abstract String getLatitude();

    public abstract void setLatitude(String latitude);

    public abstract LatLng getPosition();

    public abstract Boolean isFavorite();

    public abstract void setFavorite(Boolean favorite) ;
}
