package com.app.tomeetme.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Notification {

    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("subject")
    @Expose
    private String subject;
    @SerializedName("body")
    @Expose
    private String body;
    @SerializedName("read")
    @Expose
    private Boolean read;
    @SerializedName("applicationUserId")
    @Expose
    private String applicationUserId;
    @SerializedName("id")
    @Expose
    private String id;

    /**
     * 
     * @return
     *     The date
     */
    public String getDate() {
        return date;
    }

    /**
     * 
     * @param date
     *     The date
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * 
     * @return
     *     The subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * 
     * @param subject
     *     The subject
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * 
     * @return
     *     The body
     */
    public String getBody() {
        return body;
    }

    /**
     * 
     * @param body
     *     The body
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * 
     * @return
     *     The read
     */
    public Boolean getRead() {
        return read;
    }

    /**
     * 
     * @param read
     *     The read
     */
    public void setRead(Boolean read) {
        this.read = read;
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

}
