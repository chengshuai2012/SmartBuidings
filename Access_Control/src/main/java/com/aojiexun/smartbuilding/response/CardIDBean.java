package com.aojiexun.smartbuilding.response;

import io.realm.RealmObject;

/**
 * Created by 49488 on 2018/11/17.
 */

public class CardIDBean extends RealmObject{


    /**
     * id_card : 610427199312173653
     * project_id :
     * rfid_number : 03184911
     */

    private String id_card;
    private String project_id;
    private String rfid_number;

    public String getId_card() {
        return id_card;
    }

    public void setId_card(String id_card) {
        this.id_card = id_card;
    }

    public String getProject_id() {
        return project_id;
    }

    public void setProject_id(String project_id) {
        this.project_id = project_id;
    }

    public String getRfid_number() {
        return rfid_number;
    }

    public void setRfid_number(String rfid_number) {
        this.rfid_number = rfid_number;
    }
}
