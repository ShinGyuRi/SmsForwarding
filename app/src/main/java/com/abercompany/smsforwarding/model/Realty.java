package com.abercompany.smsforwarding.model;

import com.google.gson.annotations.SerializedName;

public class Realty {

    @SerializedName("realty_name")
    private String realtyName;

    @SerializedName("realty_broker_name")
    private String realtyBrokerName;

    @SerializedName("realty_broker_phone_num")
    private String realtyBrokerPhoneNum;

    @SerializedName("realty_account")
    private String realtyAccount;

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
}
