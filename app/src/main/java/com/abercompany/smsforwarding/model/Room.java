package com.abercompany.smsforwarding.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Room implements Serializable{

    @SerializedName("room_num")
    private String roomNum;

    @SerializedName("active")
    private String active;

    @SerializedName("price")
    private String price;

    @SerializedName("building_name")
    private String buildingName;

    @SerializedName("floor")
    private String floor;

    public String getRoomNum() {
        return roomNum;
    }
    public String getActive() {
        return active;
    }
    public String getPrice() {
        return price;
    }
    public String getBuildingName() {
        return buildingName;
    }
    public String getFloor() {
        return floor;
    }
}
