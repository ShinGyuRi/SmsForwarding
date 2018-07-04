package com.abercompany.smsforwarding.model;

import java.util.List;

public class SelectedSpinnerEvent {

    private List<String> name;
    private List<String> date;
    private List<String> objectName;
    private List<String> type;
    private List<String> startDate;
    private List<String> endDate;
    private List<Integer> positions;

    public SelectedSpinnerEvent(List<String> name, List<String> date, List<String> objectName, List<String> type, List<String> startDate, List<String> endDate, List<Integer> positions)   {
        this.name = name;
        this.date = date;
        this.objectName = objectName;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.positions = positions;
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

    public List<String> getStartDate() {
        return startDate;
    }

    public List<String> getEndDate() {
        return endDate;
    }

    public List<Integer> getPositions() {
        return positions;
    }

    //    private String name;
//    private String date;
//    private String objectName;
//    private String type;
//
//    public SelectedSpinnerEvent(String name, String date, String objectName, String type) {
//        this.name = name;
//        this.date = date;
//        this.objectName = objectName;
//        this.type = type;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public String getDate() {
//        return date;
//    }
//
//    public String getObjectName() {
//        return objectName;
//    }
//
//    public String getType() {
//        return type;
//    }
}
