package com.abercompany.smsforwarding.model;

import com.google.gson.annotations.SerializedName;

public class CheckOut {

    @SerializedName("room_num")
    private String roomNum;

    @SerializedName("name")
    private String name;

    @SerializedName("elec_amount")
    private int elecAmount;

    @SerializedName("gas_amount")
    private int gasAmount;

    @SerializedName("deposit")
    private int deposit;

    @SerializedName("out_date")
    private String outDate;

    @SerializedName("remote_con")
    private Boolean remoteCon;

    @SerializedName("account")
    private Boolean account;

    @SerializedName("katok")
    private Boolean katok;

    @SerializedName("tv")
    private Boolean tv;

    @SerializedName("day_amount")
    private int dayAmount;

    public String getRoomNum() {
        return roomNum;
    }
    public String getName() {
        return name;
    }
    public int getElecAmount() {
        return elecAmount;
    }
    public int getGasAmount() {
        return gasAmount;
    }
    public int getDeposit() {
        return deposit;
    }
    public String getOutDate() {
        return outDate;
    }
    public Boolean getRemoteCon() {
        return remoteCon;
    }
    public Boolean getAccount() {
        return account;
    }
    public Boolean getKatok() {
        return katok;
    }
    public Boolean getTv() {
        return tv;
    }
    public int getDayAmount() {
        return dayAmount;
    }
}
