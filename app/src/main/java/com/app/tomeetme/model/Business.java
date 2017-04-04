package com.app.tomeetme.model;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.app.tomeetme.data.AppDatabaseManager;
import com.app.tomeetme.data.dao.BusinessAddressDAO;
import com.app.tomeetme.data.dao.BusinessDAO;
import com.app.tomeetme.data.dao.BusinessDaysScheduleDAO;
import com.app.tomeetme.data.dao.BusinessPictureDAO;
import com.app.tomeetme.data.dao.BusinessReviewDAO;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "Business")
public class Business{

    @DatabaseField(id = true)
    @SerializedName("id")
    @Expose
    private String id;

    @DatabaseField()
    @SerializedName("phoneNumber")
    @Expose
    private String phoneNumber;

    @DatabaseField()
    @SerializedName("webSiteUrl")
    @Expose
    private String webSiteUrl;

    @DatabaseField()
    @SerializedName("emailAddress")
    @Expose
    private String emailAddress;

    @DatabaseField()
    @SerializedName("description")
    @Expose
    private String description;

    @DatabaseField()
    @SerializedName("Name")
    @Expose
    private String title;

    @DatabaseField()
    @SerializedName("reviewAverageStars")
    @Expose
    private Double reviewAverageStars;

    @DatabaseField()
    @SerializedName("applicationUserId")
    @Expose
    private String applicationUserId;

    @DatabaseField(foreign = true, index = true)
    @SerializedName("businessCategory")
    @Expose
    private BusinessCategory businessCategory;

    @DatabaseField
    public Boolean favorite;

    @DatabaseField
    public Boolean offer;

    @SerializedName("businessReviews")
    @Expose
    private List<BusinessReview> businessReviews = new ArrayList<>();

    @SerializedName("businessPictures")
    @Expose
    private List<BusinessPictures> businessPictures = new ArrayList<>();

    @SerializedName("businessAddress")
    @Expose
    @ForeignCollectionField
    private List<BusinessAddress> businessAddress = new ArrayList<>();

    @SerializedName("businessDaysSchedule")
    @Expose
    @ForeignCollectionField
    private List<BusinessDaysSchedule> businessDaysSchedule = new ArrayList<>();

    @ForeignCollectionField
    private ForeignCollection<BusinessReview> businessReviewsdb;

    @ForeignCollectionField
    private ForeignCollection<BusinessPictures> businessPicturesdb;

    @ForeignCollectionField
    private ForeignCollection<BusinessAddress> businessAddressdb;

    @ForeignCollectionField
    private ForeignCollection<BusinessDaysSchedule> businessDaysScheduledb;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getWebSiteUrl() {
        return webSiteUrl;
    }

    public void setWebSiteUrl(String webSiteUrl) {
        this.webSiteUrl = webSiteUrl;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getReviewAverageStars() {
        return reviewAverageStars;
    }

    public void setReviewAverageStars(Double reviewAverageStars) {
        this.reviewAverageStars = reviewAverageStars;
    }

    public String getApplicationUserId() {
        return applicationUserId;
    }

    public void setApplicationUserId(String applicationUserId) {
        this.applicationUserId = applicationUserId;
    }

    public Boolean isFavorite() {
        return favorite == null ? false : favorite;
    }

    public void setFavorite(Boolean favorite) {
        this.favorite = favorite;
    }

    public Boolean isOffer() {
        return offer;
    }

    public void setOffer(Boolean offer) {
        this.offer = offer;
    }

    public BusinessCategory getBusinessCategory() {
        return businessCategory;
    }

    public void setBusinessCategory(BusinessCategory businessCategory) {
        this.businessCategory = businessCategory;
    }

    public List<BusinessReview> getBusinessReviews() {
        return businessReviews;
    }

    public void setBusinessReviews(List<BusinessReview> businessReviews) {
        this.businessReviews = businessReviews;
    }

    public List<BusinessPictures> getBusinessPictures() {
        return businessPictures;
    }

    public void setBusinessPictures(List<BusinessPictures> businessPictures) {
        this.businessPictures = businessPictures;
    }

    public List<BusinessAddress> getBusinessAddress() {
        return businessAddress;
    }

    public void setBusinessAddress(List<BusinessAddress> businessAddress) {
        this.businessAddress = businessAddress;
    }

    public List<BusinessDaysSchedule> getBusinessDaysSchedule() {
        return businessDaysSchedule;
    }

    public void setBusinessDaysSchedule(List<BusinessDaysSchedule> businessDaysSchedule) {
        this.businessDaysSchedule = businessDaysSchedule;
    }

    public ForeignCollection<BusinessReview> getBusinessReviewsdb() {
        return businessReviewsdb;
    }

    public void setBusinessReviewsdb(ForeignCollection<BusinessReview> businessReviewsdb) {
        this.businessReviewsdb = businessReviewsdb;
    }

    public ForeignCollection<BusinessPictures> getBusinessPicturesdb() {
        return businessPicturesdb;
    }

    public void setBusinessPicturesdb(ForeignCollection<BusinessPictures> businessPicturesdb) {
        this.businessPicturesdb = businessPicturesdb;
    }

    public ForeignCollection<BusinessAddress> getBusinessAddressdb() {
        return businessAddressdb;
    }

    public void setBusinessAddressdb(ForeignCollection<BusinessAddress> businessAddressdb) {
        this.businessAddressdb = businessAddressdb;
    }

    public ForeignCollection<BusinessDaysSchedule> getBusinessDaysScheduledb() {
        return businessDaysScheduledb;
    }

    public void setBusinessDaysScheduledb(ForeignCollection<BusinessDaysSchedule> businessDaysScheduledb) {
        this.businessDaysScheduledb = businessDaysScheduledb;
    }

    public void SyncrhonizeTo() {

        if (getBusinessReviews() != null) {
            try {
                businessReviewsdb = new BusinessDAO(AppDatabaseManager.getInstance().getHelper()).getDao().getEmptyForeignCollection("businessReviewsdb");

                for (BusinessReview item : getBusinessReviews()) {
                    if (new BusinessReviewDAO(AppDatabaseManager.getInstance().getHelper()).Get(item) == null)
                        businessReviewsdb.add(item);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (getBusinessPictures() != null) {
            try {
                businessPicturesdb = new BusinessDAO(AppDatabaseManager.getInstance().getHelper()).getDao().getEmptyForeignCollection("businessPicturesdb");

                for (BusinessPictures item : getBusinessPictures()) {
                    if (new BusinessPictureDAO(AppDatabaseManager.getInstance().getHelper()).Get(item) == null)
                        businessPicturesdb.add(item);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (getBusinessAddress() != null) {
            try {
                businessAddressdb = new BusinessDAO(AppDatabaseManager.getInstance().getHelper()).getDao().getEmptyForeignCollection("businessAddressdb");
                for (BusinessAddress item : getBusinessAddressdb()) {
                    if (new BusinessAddressDAO(AppDatabaseManager.getInstance().getHelper()).Get(item) == null)
                        businessAddressdb.add(item);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (getBusinessDaysSchedule() != null) {
            try {
                businessDaysScheduledb = new BusinessDAO(AppDatabaseManager.getInstance().getHelper()).getDao().getEmptyForeignCollection("businessDaysScheduledb");
                for (BusinessDaysSchedule item : getBusinessDaysScheduledb()) {
                    if (new BusinessDaysScheduleDAO(AppDatabaseManager.getInstance().getHelper()).Get(item) == null)
                        businessDaysScheduledb.add(item);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void SyncrhonizeFrom() {

        if (getBusinessReviewsdb() != null)
            businessReviews = new ArrayList<>(getBusinessReviewsdb());

        if (getBusinessPicturesdb() != null)
            businessPictures = new ArrayList<>(getBusinessPicturesdb());

        if (getBusinessAddressdb() != null)
            businessAddress = new ArrayList<>(getBusinessAddressdb());

        if (getBusinessDaysScheduledb() != null)
            businessDaysSchedule = new ArrayList<>(getBusinessDaysScheduledb());

    }
}

