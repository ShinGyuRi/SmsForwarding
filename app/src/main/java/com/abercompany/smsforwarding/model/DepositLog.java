package com.abercompany.smsforwarding.model;

import com.google.gson.annotations.SerializedName;

public class DepositLog {

    @SerializedName("start_date")
    private String startDate;

    @SerializedName("end_date")
    private String endDate;

    @SerializedName("deposit_date")
    private String depositDate;

    @SerializedName("amount")
    private String amount;

    public String getStartDate() {
        return startDate;
    }
    public String getEndDate() {
        return endDate;
    }
    public String getDepositDate() {
        return depositDate;
    }
    public String getAmount() {
        return amount;
    }
}
