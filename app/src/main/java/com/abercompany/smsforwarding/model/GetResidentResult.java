package com.abercompany.smsforwarding.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetResidentResult {

    @SerializedName("result")
    private String result;

    @SerializedName("message")
    private List<Resident> residents;

    public String getResult() {
        return result;
    }
    public void setResult(String result) {
        this.result = result;
    }

    public List<Resident> getResidents() {
        return residents;
    }
    public void setResidents(List<Resident> residents) {
        this.residents = residents;
    }
}
