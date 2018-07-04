package com.abercompany.smsforwarding.model;

import com.google.gson.annotations.SerializedName;

public class Defaulter {

    @SerializedName("dst_name")
    private String dstName;

    @SerializedName("end_date")
    private String endDate;

    public String getDstName() {
        return dstName;
    }

    public String getEndDate() {
        return endDate;
    }
}
