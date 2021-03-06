package com.abercompany.smsforwarding.model;

import com.google.gson.annotations.SerializedName;

public class Building {

    @SerializedName("name")
    private String name;

    @SerializedName("count_use_room")
    private int countUseRoom;

    @SerializedName("count_total_room")
    private int countTotalRoom;

    public String getName() {
        return name;
    }
    public int getCountUseRoom() {
        return countUseRoom;
    }
    public int getCountTotalRoom() {
        return countTotalRoom;
    }
}
