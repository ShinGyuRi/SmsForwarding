package com.abercompany.smsforwarding.model;

import com.google.gson.annotations.SerializedName;

public class GetCheckOutListResult {

    @SerializedName("result")
    private String result;

    @SerializedName("message")
    private CheckOut checkOut;

    public String getResult() {
        return result;
    }
    public CheckOut getCheckOut() {
        return checkOut;
    }
}
