package com.abercompany.smsforwarding.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Defaulter implements Serializable{

    @SerializedName("dst_name")
    private String dstName;

    @SerializedName("end_date")
    private String endDate;

    @SerializedName("room_num")
    private String roomNum;

    @SerializedName("name")
    private String name;

    @SerializedName("status")
    private String status;

    @SerializedName("building_name")
    private String buildingName;

    @SerializedName("elec_num")
    private String elecNum;

    @SerializedName("amount")
    private String amount;

    public String getDstName() {
        return dstName;
    }
    public String getEndDate() {
        return endDate;
    }
    public String getRoomNum() {
        return roomNum;
    }
    public String getName() {
        return name;
    }
    public String getStatus() {
        return status;
    }
    public String getBuildingName() {
        return buildingName;
    }
    public String getElecNum() {
        return elecNum;
    }
    public String getAmount() {
        return amount;
    }
}
