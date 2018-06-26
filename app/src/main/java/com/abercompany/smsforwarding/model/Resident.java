package com.abercompany.smsforwarding.model;

import com.google.gson.annotations.SerializedName;

public class Resident {

    @SerializedName("name")
    private String name;

    @SerializedName("ho")
    private String ho;

    @SerializedName("phone_num")
    private String phoneNum;

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
}
