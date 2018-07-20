package com.abercompany.smsforwarding.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetBuildingResult {

    @SerializedName("result")
    private String result;

    @SerializedName("message")
    private List<Building> buildings;

    public String getResult() {
        return result;
    }
    public List<Building> getBuildings() {
        return buildings;
    }
}
