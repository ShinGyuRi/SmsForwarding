package com.abercompany.smsforwarding.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Room implements Serializable{

    @SerializedName("room_num")
    private String roomNum;

    @SerializedName("active")
    private String active;

    public String getRoomNum() {
        return roomNum;
    }
    public String getActive() {
        return active;
    }
}
