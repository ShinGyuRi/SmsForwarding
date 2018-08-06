package com.abercompany.smsforwarding.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetElecDefaulter {

    @SerializedName("result")
    private String result;

    @SerializedName("message")
    private List<ElecDefaulter> elecDefaulterList;

    public String getResult() {
        return result;
    }
    public List<ElecDefaulter> getElecDefaulterList() {
        return elecDefaulterList;
    }
}
