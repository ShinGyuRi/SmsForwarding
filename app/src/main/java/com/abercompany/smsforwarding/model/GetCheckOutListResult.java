package com.abercompany.smsforwarding.model;

import com.google.gson.annotations.SerializedName;

public class GetCheckOutListResult {

    @SerializedName("result")
    private String result;

    @SerializedName("message")
    private CheckOutList checkOutList;

    public String getResult() {
        return result;
    }
    public CheckOutList getCheckOutList() {
        return checkOutList;
    }
}
