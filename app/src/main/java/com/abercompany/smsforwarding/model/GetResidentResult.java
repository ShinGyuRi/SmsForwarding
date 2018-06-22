package com.abercompany.smsforwarding.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetResidentResult {

    @SerializedName("result")
    private String result;

    @SerializedName("message")
    private List<Resigent> resigents;

    public String getResult() {
        return result;
    }
    public void setResult(String result) {
        this.result = result;
    }

    public List<Resigent> getResigents() {
        return resigents;
    }
    public void setResigents(List<Resigent> resigents) {
        this.resigents = resigents;
    }
}
