package com.abercompany.smsforwarding.model;

import com.google.gson.annotations.SerializedName;

public class CheckInList {

    @SerializedName("room_num")
    private String roomNum;

    @SerializedName("name")
    private String name;

    @SerializedName("id_num")
    private Boolean idNum;

    @SerializedName("emer_num")
    private Boolean emerNum;

    @SerializedName("amount")
    private Boolean amount;

    @SerializedName("elec_gas")
    private Boolean elecGas;

    @SerializedName("condition")
    private Boolean condition;

    @SerializedName("realty")
    private Boolean realty;

    public String getRoomNum() {
        return roomNum;
    }
    public String getName() {
        return name;
    }
    public Boolean isIdNum() {
        return idNum;
    }
    public Boolean isEmerNum() {
        return emerNum;
    }
    public Boolean isAmount() {
        return amount;
    }
    public Boolean isElecGas() {
        return elecGas;
    }
    public Boolean isCondition() {
        return condition;
    }
    public Boolean isRealty() {
        return realty;
    }
}
