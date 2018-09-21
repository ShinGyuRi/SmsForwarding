package com.abercompany.smsforwarding.model;

import com.google.gson.annotations.SerializedName;

public class Checkout {

    @SerializedName("room_num")
    private String roomNum;

    @SerializedName("name")
    private String name;

    @SerializedName("deposit")
    private String deposit;

    @SerializedName("rent")
    private String rent;

    @SerializedName("manage_fee")
    private String manageFee;

    @SerializedName("elec_num")
    private String elecNum;

    @SerializedName("elec_amount")
    private String elecAmount;

    @SerializedName("gas_num")
    private String gasNum;

    @SerializedName("gas_amount")
    private String gasAmount;

    @SerializedName("checkout_fee")
    private String checkoutFee;

    @SerializedName("realty_fees")
    private String realtyFees;

    @SerializedName("penalty")
    private String penalty;

    @SerializedName("discount")
    private String discount;

    @SerializedName("total")
    private String total;

    @SerializedName("account")
    private String account;

    @SerializedName("bank")
    private String bank;

    @SerializedName("date")
    private String date;

    @SerializedName("building_name")
    private String buildingName;

    @SerializedName("usage_fee")
    private String usageFee;

    public String getRoomNum() {
        return roomNum;
    }
    public String getName() {
        return name;
    }
    public String getDeposit() {
        return deposit;
    }
    public String getRent() { return rent; }
    public String getManageFee() {
        return manageFee;
    }
    public String getElecNum() {
        return elecNum;
    }
    public String getElecAmount() {
        return elecAmount;
    }
    public String getGasNum() {
        return gasNum;
    }
    public String getGasAmount() {
        return gasAmount;
    }
    public String getCheckoutFee() {
        return checkoutFee;
    }
    public String getRealtyFees() {
        return realtyFees;
    }
    public String getPenalty() {
        return penalty;
    }
    public String getDiscount() {
        return discount;
    }
    public String getTotal() {
        return total;
    }
    public String getAccount() {
        return account;
    }
    public String getBank() {
        return bank;
    }
    public String getDate() {
        return date;
    }
    public String getBuildingName() {
        return buildingName;
    }
    public String getUsageFee() {
        return usageFee;
    }
}
