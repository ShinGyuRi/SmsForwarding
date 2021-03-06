package com.abercompany.smsforwarding.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetRealtyResult {

    @SerializedName("result")
    private String result;

    @SerializedName("message")
    private List<Realty> realties;

    public String getResult() {
        return result;
    }
    public List<Realty> getRealties() {
        return realties;
    }
}
