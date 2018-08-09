package com.abercompany.smsforwarding.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Resident implements Serializable {

    @SerializedName("name")
    private String name;

    @SerializedName("ho")
    private String ho;

    @SerializedName("phone_num")
    private String phoneNum;

    @SerializedName("etc_num")
    private String etcNum;

    @SerializedName("active")
    private String active;

    @SerializedName("building_name")
    private String buildingName;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getHo() {
        return ho;
    }
    public void setHo(String ho) {
        this.ho = ho;
    }

    public String getPhoneNum() {
        return phoneNum;
    }
    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getEtcNum() {
        return etcNum;
    }
    public void setEtcNum(String etcNum) {
        this.etcNum = etcNum;
    }

    public String getActive() {
        return active;
    }
    public void setActive(String active) {
        this.active = active;
    }

    public String getBuildingName() {
        return buildingName;
    }
    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }
}
