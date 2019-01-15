package com.aojiexun.smartbuilding.request;

/**
 * Created by 49488 on 2018/12/14.
 */

public class RequestReport {

    public String getTraffic_time() {
        return traffic_time;
    }

    public void setTraffic_time(String traffic_time) {
        this.traffic_time = traffic_time;
    }

    /**
     * project_id : 16
     * id_card : 239005198909042034
     * mac_address : EA:07:68:94:BF:01
     * traffic_direction : out
     * traffic_img : dfdfdfdfdfdfdfdfdfdgdfgdfg
     * rfid : 04251658
     */

    private String project_id;
    private String id_card;
    private String mac_address;
    private String traffic_direction;
    private String traffic_time;
    private String traffic_img;
    private String rfid;

    public String getProject_id() {
        return project_id;
    }

    public void setProject_id(String project_id) {
        this.project_id = project_id;
    }

    public String getId_card() {
        return id_card;
    }

    public void setId_card(String id_card) {
        this.id_card = id_card;
    }

    public String getMac_address() {
        return mac_address;
    }

    public void setMac_address(String mac_address) {
        this.mac_address = mac_address;
    }

    public String getTraffic_direction() {
        return traffic_direction;
    }

    public void setTraffic_direction(String traffic_direction) {
        this.traffic_direction = traffic_direction;
    }

    public String getTraffic_img() {
        return traffic_img;
    }

    public void setTraffic_img(String traffic_img) {
        this.traffic_img = traffic_img;
    }

    public String getRfid() {
        return rfid;
    }

    public void setRfid(String rfid) {
        this.rfid = rfid;
    }
}
