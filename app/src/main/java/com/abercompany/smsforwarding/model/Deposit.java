package com.abercompany.smsforwarding.model;

import com.google.gson.annotations.SerializedName;

public class Deposit {

    @SerializedName("name")
    private String name;

    @SerializedName("amount")
    private String amount;

    @SerializedName("date")
    private String date;

    @SerializedName("method")
    private String method;

    @SerializedName("type")
    private String type;

    @SerializedName("destination_name")
    private String destinationName;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getAmount() {
        return amount;
    }
    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public String getMethod() {
        return method;
    }
    public void setMethod(String method) {
        this.method = method;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public String getDestinationName() {
        return destinationName;
    }
    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }
}
