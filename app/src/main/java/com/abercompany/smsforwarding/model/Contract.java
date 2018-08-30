package com.abercompany.smsforwarding.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Contract implements Serializable{

    @SerializedName("room_num")
    private String roomNum;

    @SerializedName("name")
    private String name;

    @SerializedName("down_payment")
    private String downPayment;

    @SerializedName("deposit")
    private String deposit;

    @SerializedName("rent")
    private String rent;

    @SerializedName("management_fee")
    private String manageFee;

    @SerializedName("start_date")
    private String startDate;

    @SerializedName("end_date")
    private String endDate;

    @SerializedName("active")
    private String active;

    @SerializedName("phone_num")
    private String phoneNum;

    @SerializedName("etc_num")
    private String etcNum;

    @SerializedName("emer_num")
    private String emerNum;

    @SerializedName("emer_name")
    private String emerName;

    @SerializedName("address")
    private String address;

    @SerializedName("id_num")
    private String idNum;

    @SerializedName("elec_num")
    private String elecNum;

    @SerializedName("gas_num")
    private String gasNum;

    @SerializedName("realty_name")
    private String realtyName;

    @SerializedName("realty_broker_name")
    private String realtyBrokerName;

    @SerializedName("realty_broker_phone_num")
    private String realtyBrokerPhoneNum;

    @SerializedName("realty_account")
    private String realtyAccount;

    @SerializedName("building_name")
    private String buildingName;

    public String getRoomNum() {
        return roomNum;
    }
    public String getName() {
        return name;
    }
    public String getDownPayment() {
        return downPayment;
    }
    public String getDeposit() {
        return deposit;
    }
    public String getRent() {
        return rent;
    }
    public String getManageFee() {
        return manageFee;
    }
    public String getStartDate() {
        return startDate;
    }
    public String getEndDate() {
        return endDate;
    }
    public String getActive() {
        return active;
    }
    public String getPhoneNum() {
        return phoneNum;
    }
    public String getEtcNum() {
        return etcNum;
    }
    public String getEmerNum() {
        return emerNum;
    }
    public String getEmerName() {
        return emerName;
    }
    public String getAddress() {
        return address;
    }
    public String getIdNum() {
        return idNum;
    }
    public String getElecNum() {
        return elecNum;
    }
    public String getGasNum() {
        return gasNum;
    }
    public String getRealtyName() {
        return realtyName;
    }
    public String getRealtyBrokerName() {
        return realtyBrokerName;
    }
    public String getRealtyBrokerPhoneNum() {
        return realtyBrokerPhoneNum;
    }
    public String getRealtyAccount() {
        return realtyAccount;
    }
    public String getBuildingName() {
        return buildingName;
    }
}
