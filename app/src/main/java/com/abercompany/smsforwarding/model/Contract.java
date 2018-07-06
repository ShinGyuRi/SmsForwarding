package com.abercompany.smsforwarding.model;

import com.google.gson.annotations.SerializedName;

public class Contract {

    @SerializedName("room_num")
    private String roomNum;

    @SerializedName("name")
    private String name;

    @SerializedName("deposit")
    private String deposit;

    @SerializedName("rent")
    private String rent;

    @SerializedName("management_fee")
    private String manageFee;

    @SerializedName("start_date")
    private String startDate;

    @SerializedName("end_date")
    private String endDate;

    @SerializedName("active")
    private String active;

    public String getRoomNum() {
        return roomNum;
    }
    public String getName() {
        return name;
    }
    public String getDeposit() {
        return deposit;
    }
    public String getRent() {
        return rent;
    }
    public String getManageFee() {
        return manageFee;
    }
    public String getStartDate() {
        return startDate;
    }
    public String getEndDate() {
        return endDate;
    }
    public String getActive() {
        return active;
    }
}
