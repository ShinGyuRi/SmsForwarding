package com.abercompany.smsforwarding.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetDepositResult {

    @SerializedName("result")
    private String result;

    @SerializedName("message")
    private List<Deposit> message;

    public String getResult() {
        return result;
    }
    public void setResult(String result) {
        this.result = result;
    }

    public List<Deposit> getMessage() {
        return message;
    }
    public void setMessage(List<Deposit> message) {
        this.message = message;
    }
}
