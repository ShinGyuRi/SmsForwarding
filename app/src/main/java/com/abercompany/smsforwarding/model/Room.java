package com.abercompany.smsforwarding.model;

import com.google.gson.annotations.SerializedName;

public class Room {

    @SerializedName("room_num")
    private String roomNum;

    public String getRoomNum() {
        return roomNum;
    }
}
