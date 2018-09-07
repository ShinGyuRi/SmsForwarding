package com.abercompany.smsforwarding.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Deposit implements Serializable {

    @SerializedName("index")
    private String index;

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

    @SerializedName("note")
    private String note;

    @SerializedName("time_stamp")
    private String timeStamp;

    @SerializedName("building_name")
    private String buildingName;

    @SerializedName("account")
    private String account;

    @SerializedName("dst_name")
    private String dstName;

    private int viewPosition;

    private int spCategorySelectedPosition;

    private int spTonameSelectedPosition;

    public String getIndex() {
        return index;
    }
    public void setIndex(String index) {
        this.index = index;
    }

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

    public String getNote() {
        return note;
    }
    public void setNote(String note) {
        this.note = note;
    }

    public String getTimeStamp() {
        return timeStamp;
    }
    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getViewPosition() {
        return viewPosition;
    }
    public void setViewPosition(int viewPosition) {
        this.viewPosition = viewPosition;
    }

    public String getBuildingName() {
        return buildingName;
    }
    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public String getAccount() {
        return account;
    }
    public void setAccount(String account) {
        this.account = account;
    }

    public String getDstName() {
        return dstName;
    }
    public void setDstName(String dstName) {
        this.dstName = dstName;
    }

    public int getSpCategorySelectedPosition() {
        return spCategorySelectedPosition;
    }
    public void setSpCategorySelectedPosition(int spCategorySelectedPosition) {
        this.spCategorySelectedPosition = spCategorySelectedPosition;
    }

    public int getSpTonameSelectedPosition() {
        return spTonameSelectedPosition;
    }
    public void setSpTonameSelectedPosition(int spTonameSelectedPosition) {
        this.spTonameSelectedPosition = spTonameSelectedPosition;
    }
}
