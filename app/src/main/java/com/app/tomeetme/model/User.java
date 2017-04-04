
package com.app.tomeetme.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.ArrayList;
import java.util.List;

@DatabaseTable(tableName = "User")
public class User {

    @DatabaseField(id = true)
    private String id;
    @DatabaseField(canBeNull = true)
    private boolean owner;
    @DatabaseField(canBeNull = true)
    private String formattedAddress;
    @DatabaseField(canBeNull = true)
    private String lastName;
    @DatabaseField(canBeNull = true)
    private String firstName;
    @DatabaseField(canBeNull = true)
    private String roleId;
    @DatabaseField(canBeNull = true)
    private String phoneNumber;
    @DatabaseField(canBeNull = true)
    private String email;
    @DatabaseField(canBeNull = true)
    private String saltedAndHashedPassword;
    @DatabaseField(canBeNull = true)
    private String salt;
    @DatabaseField(canBeNull = true)
    private Boolean active;
    @DatabaseField(canBeNull = true)
    private String token;
    @SerializedName("businesses")
    @Expose
    private List<Business> businesses = new ArrayList<>();
    @SerializedName("notifications")
    @Expose
    private List<Notification> notifications = new ArrayList<>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public User() {
    }

    /**
     * 
     * @param id
     * @param lastName
     * @param phoneNumber
     * @param email
     * @param owner
     * @param saltedAndHashedPassword
     * @param formattedAddress
     * @param firstName
     * @param salt
     * @param roleId
     */
    public User(String id, boolean owner, String formattedAddress, String lastName, String firstName, String roleId, String phoneNumber, String email, String saltedAndHashedPassword, String salt) {
        this.id = id;
        this.owner = owner;
        this.formattedAddress = formattedAddress;
        this.lastName = lastName;
        this.firstName = firstName;
        this.roleId = roleId;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.saltedAndHashedPassword = saltedAndHashedPassword;
        this.salt = salt;

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
     *     The owner
     */
    public boolean isOwner() {
        return owner;
    }

    /**
     * 
     * @param owner
     *     The owner
     */
    public void setOwner(boolean owner) {
        this.owner = owner;
    }

    /**
     * 
     * @return
     *     The formattedAddress
     */
    public Object getFormattedAddress() {
        return formattedAddress;
    }

    /**
     * 
     * @param formattedAddress
     *     The formattedAddress
     */
    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }

    /**
     * 
     * @return
     *     The lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * 
     * @param lastName
     *     The lastName
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * 
     * @return
     *     The firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * 
     * @param firstName
     *     The firstName
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * 
     * @return
     *     The roleId
     */
    public String getRoleId() {
        return roleId;
    }

    /**
     * 
     * @param roleId
     *     The roleId
     */
    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    /**
     * 
     * @return
     *     The phoneNumber
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * 
     * @param phoneNumber
     *     The phoneNumber
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * 
     * @return
     *     The email
     */
    public String getEmail() {
        return email;
    }

    /**
     * 
     * @param email
     *     The email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * 
     * @return
     *     The saltedAndHashedPassword
     */
    public String getSaltedAndHashedPassword() {
        return saltedAndHashedPassword;
    }

    /**
     * 
     * @param saltedAndHashedPassword
     *     The saltedAndHashedPassword
     */
    public void setSaltedAndHashedPassword(String saltedAndHashedPassword) {
        this.saltedAndHashedPassword = saltedAndHashedPassword;
    }

    /**
     * 
     * @return
     *     The salt
     */
    public String getSalt() {
        return salt;
    }

    /**
     * 
     * @param salt
     *     The salt
     */
    public void setSalt(String salt) {
        this.salt = salt;
    }

    /**
     *
     * @return
     *     The businesses
     */
    public List<Business> getBusinesses() {
        return businesses;
    }

    /**
     *
     * @param businesses
     *     The businesses
     */
    public void setBusinesses(List<Business> businesses) {
        this.businesses = businesses;
    }


    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
