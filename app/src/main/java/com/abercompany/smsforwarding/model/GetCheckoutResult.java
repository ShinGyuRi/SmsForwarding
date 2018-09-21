package com.abercompany.smsforwarding.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetCheckoutResult {

    @SerializedName("result")
    private String result;

    @SerializedName("message")
    private Checkout checkout;

    public String getResult() {
        return result;
    }
    public Checkout getCheckout() {
        return checkout;
    }
}
