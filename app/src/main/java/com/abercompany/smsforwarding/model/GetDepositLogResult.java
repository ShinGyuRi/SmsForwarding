package com.abercompany.smsforwarding.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetDepositLogResult {

    @SerializedName("result")
    private String result;

    @SerializedName("message")
    private List<DepositLog> depositLogs;

    public String getResult() {
        return result;
    }
    public List<DepositLog> getDepositLogs() {
        return depositLogs;
    }
}
