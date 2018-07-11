package com.abercompany.smsforwarding.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Broker implements Serializable{

    @SerializedName("realty_name")
    private String realtyName;

    @SerializedName("name")
    private String name;

    @SerializedName("phone_num")
    private String phoneNum;

    public String getRealtyName() {
        return realtyName;
    }
    public void setRealtyName(String realtyName) {
        this.realtyName = realtyName;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNum() {
        return phoneNum;
    }
    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }
}
