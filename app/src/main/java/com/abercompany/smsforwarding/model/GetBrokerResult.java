package com.abercompany.smsforwarding.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetBrokerResult {

    @SerializedName("result")
    private String result;

    @SerializedName("message")
    private List<Broker> brokers;

    public String getResult() {
        return result;
    }
    public void setResult(String result) {
        this.result = result;
    }

    public List<Broker> getBrokers() {
        return brokers;
    }
    public void setBrokers(List<Broker> brokers) {
        this.brokers = brokers;
    }
}
