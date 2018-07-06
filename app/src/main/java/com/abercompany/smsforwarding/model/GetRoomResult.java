package com.abercompany.smsforwarding.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetRoomResult {

    @SerializedName("result")
    private String result;

    @SerializedName("message")
    private List<Room> rooms;

    public String getResult() {
        return result;
    }

    public List<Room> getRooms() {
        return rooms;
    }
}
