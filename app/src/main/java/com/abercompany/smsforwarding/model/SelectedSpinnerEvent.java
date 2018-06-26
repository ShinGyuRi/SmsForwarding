package com.abercompany.smsforwarding.model;

import java.util.List;

public class SelectedSpinnerEvent {

    private List<String> name;
    private List<String> date;
    private List<String> objectName;
    private List<String> type;

    public SelectedSpinnerEvent(List<String> name, List<String> date, List<String> objectName, List<String> type)   {
        this.name = name;
        this.date = date;
        this.objectName = objectName;
        this.type = type;
    }

    public List<String> getName() {
        return name;
    }
    public List<String> getDate() {
        return date;
    }
    public List<String> getObjectName() {
        return objectName;
    }
    public List<String> getType() {
        return type;
    }
}
