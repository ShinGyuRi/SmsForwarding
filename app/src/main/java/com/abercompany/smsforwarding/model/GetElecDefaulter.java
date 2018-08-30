package com.abercompany.smsforwarding.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetElecDefaulter {

    @SerializedName("result")
    private String result;

    @SerializedName("message")
    private List<Defaulter> elecDefaulterList;

    public String getResult() {
        return result;
    }
    public List<Defaulter> getElecDefaulterList() {
        return elecDefaulterList;
    }
}
