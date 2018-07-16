package com.abercompany.smsforwarding.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetCheckInListResult {

    @SerializedName("result")
    private String result;

    @SerializedName("message")
    private CheckIn checkIns;

    public String getResult() {
        return result;
    }
    public CheckIn getCheckIns() {
        return checkIns;
    }
}
