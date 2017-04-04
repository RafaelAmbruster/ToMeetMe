package com.app.tomeetme.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Country {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("internationalRegion")
    @Expose
    private String internationalRegion;
    @SerializedName("cctld")
    @Expose
    private String cctld;
    @SerializedName("callingCode")
    @Expose
    private String callingCode;
    @SerializedName("unMemberState")
    @Expose
    private String unMemberState;
    @SerializedName("numCode")
    @Expose
    private String numCode;
    @SerializedName("iso3")
    @Expose
    private String iso3;
    @SerializedName("longCountryName")
    @Expose
    private String longCountryName;
    @SerializedName("iso2")
    @Expose
    private String iso2;
    @SerializedName("countryName")
    @Expose
    private String countryName;

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
     *     The internationalRegion
     */
    public String getInternationalRegion() {
        return internationalRegion;
    }

    /**
     * 
     * @param internationalRegion
     *     The internationalRegion
     */
    public void setInternationalRegion(String internationalRegion) {
        this.internationalRegion = internationalRegion;
    }

    /**
     * 
     * @return
     *     The cctld
     */
    public String getCctld() {
        return cctld;
    }

    /**
     * 
     * @param cctld
     *     The cctld
     */
    public void setCctld(String cctld) {
        this.cctld = cctld;
    }

    /**
     * 
     * @return
     *     The callingCode
     */
    public String getCallingCode() {
        return callingCode;
    }

    /**
     * 
     * @param callingCode
     *     The callingCode
     */
    public void setCallingCode(String callingCode) {
        this.callingCode = callingCode;
    }

    /**
     * 
     * @return
     *     The unMemberState
     */
    public String getUnMemberState() {
        return unMemberState;
    }

    /**
     * 
     * @param unMemberState
     *     The unMemberState
     */
    public void setUnMemberState(String unMemberState) {
        this.unMemberState = unMemberState;
    }

    /**
     * 
     * @return
     *     The numCode
     */
    public String getNumCode() {
        return numCode;
    }

    /**
     * 
     * @param numCode
     *     The numCode
     */
    public void setNumCode(String numCode) {
        this.numCode = numCode;
    }

    /**
     * 
     * @return
     *     The iso3
     */
    public String getIso3() {
        return iso3;
    }

    /**
     * 
     * @param iso3
     *     The iso3
     */
    public void setIso3(String iso3) {
        this.iso3 = iso3;
    }

    /**
     * 
     * @return
     *     The longCountryName
     */
    public String getLongCountryName() {
        return longCountryName;
    }

    /**
     * 
     * @param longCountryName
     *     The longCountryName
     */
    public void setLongCountryName(String longCountryName) {
        this.longCountryName = longCountryName;
    }

    /**
     * 
     * @return
     *     The iso2
     */
    public String getIso2() {
        return iso2;
    }

    /**
     * 
     * @param iso2
     *     The iso2
     */
    public void setIso2(String iso2) {
        this.iso2 = iso2;
    }

    /**
     * 
     * @return
     *     The countryName
     */
    public String getCountryName() {
        return countryName;
    }

    /**
     * 
     * @param countryName
     *     The countryName
     */
    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

}
