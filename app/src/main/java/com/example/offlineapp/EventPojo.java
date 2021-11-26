package com.example.offlineapp;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

class EventPojo {

    @SerializedName("event")
    @Expose
    private Event event;

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

}

 class CreatedByUser {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("data")
    @Expose
    private List<Integer> data = null;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Integer> getData() {
        return data;
    }

    public void setData(List<Integer> data) {
        this.data = data;
    }

}

 class Event {

    @SerializedName("id")
    @Expose
    private ID id;
    @SerializedName("user_id")
    @Expose
    private UsersId usersId;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("created_by")
    @Expose
    private CreatedByUser createdByUser;

    public ID getId() {
        return id;
    }

    public void setId(ID id) {
        this.id = id;
    }

    public UsersId getUserId() {
        return usersId;
    }

    public void setUserId(UsersId usersId) {
        this.usersId = usersId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public CreatedByUser getCreatedBy() {
        return createdByUser;
    }

    public void setCreatedBy(CreatedByUser createdByUser) {
        this.createdByUser = createdByUser;
    }

}

 class ID {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("data")
    @Expose
    private List<Integer> data = null;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Integer> getData() {
        return data;
    }

    public void setData(List<Integer> data) {
        this.data = data;
    }

}

 class UsersId {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("data")
    @Expose
    private List<Integer> data = null;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Integer> getData() {
        return data;
    }

    public void setData(List<Integer> data) {
        this.data = data;
    }

}