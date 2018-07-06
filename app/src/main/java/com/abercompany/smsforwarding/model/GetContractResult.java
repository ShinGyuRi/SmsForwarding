package com.abercompany.smsforwarding.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetContractResult {

    @SerializedName("result")
    private String result;

    @SerializedName("message")
    private List<Contract> contracts;

    public String getResult() {
        return result;
    }

    public List<Contract> getContracts() {
        return contracts;
    }
}
