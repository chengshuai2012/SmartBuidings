package com.aojiexun.smartbuilding.response;

/**
 * Created by 49488 on 2018/12/14.
 */

public class ProjectID {

    /**
     * amount : 1000
     * area : 1000
     * function : 商业建筑
     * id : 16
     * name : 海府大厦
     * plies : 10
     * porjectCode : 44030120180831003
     * time : 2018-11-30 17:34:35
     * type : 商业建筑
     */

    private double amount;
    private double area;
    private String function;
    private int id;
    private String name;
    private String location;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    private int plies;
    private String porjectCode;
    private String time;
    private String type;

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPlies() {
        return plies;
    }

    public void setPlies(int plies) {
        this.plies = plies;
    }

    public String getPorjectCode() {
        return porjectCode;
    }

    public void setPorjectCode(String porjectCode) {
        this.porjectCode = porjectCode;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
