package com.app.tomeetme.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BusinessFavorites {

    @SerializedName("$id")
    @Expose
    private String $id;
    @SerializedName("applicationUserId")
    @Expose
    private String applicationUserId;
    @SerializedName("businessId")
    @Expose
    private String businessId;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("$ref")
    @Expose
    private String $ref;

    /**
     * 
     * @return
     *     The $id
     */
    public String get$id() {
        return $id;
    }

    /**
     * 
     * @param $id
     *     The $id
     */
    public void set$id(String $id) {
        this.$id = $id;
    }

    /**
     * 
     * @return
     *     The applicationUserId
     */
    public String getApplicationUserId() {
        return applicationUserId;
    }

    /**
     * 
     * @param applicationUserId
     *     The applicationUserId
     */
    public void setApplicationUserId(String applicationUserId) {
        this.applicationUserId = applicationUserId;
    }

    /**
     * 
     * @return
     *     The businessId
     */
    public String getBusinessId() {
        return businessId;
    }

    /**
     * 
     * @param businessId
     *     The businessId
     */
    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    /**
     * 
     * @return
     *     The id
     */
    public String getId() {
        return id;
    }

    /**
     * 
     * @param id
     *     The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 
     * @return
     *     The $ref
     */
    public String get$ref() {
        return $ref;
    }

    /**
     * 
     * @param $ref
     *     The $ref
     */
    public void set$ref(String $ref) {
        this.$ref = $ref;
    }

}
