package com.abercompany.smsforwarding.model;

import com.google.gson.annotations.SerializedName;

public class ElecDefaulter {

    @SerializedName("room_num")
    private String roomNum;

    @SerializedName("name")
    private String name;

    @SerializedName("check_date")
    private String checkDate;

    public String getRoomNum() {
        return roomNum;
    }
    public String getName() {
        return name;
    }
    public String getCheckDate() {
        return checkDate;
    }
}
