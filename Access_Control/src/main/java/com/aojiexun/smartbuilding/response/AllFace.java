package com.aojiexun.smartbuilding.response;

/**
 * Created by 49488 on 2018/12/14.
 */

public class AllFace {


    /**
     * face_url : http://devicepackage.oss-cn-shenzhen.aliyuncs.com/239005198909042034.data
     * id : 7
     * id_card : 239005198909042034
     * name : 李长城
     */

    private String face_url;
    private int id;
    private String id_card;
    private String name;

    public String getFace_url() {
        return face_url;
    }

    public void setFace_url(String face_url) {
        this.face_url = face_url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getId_card() {
        return id_card;
    }

    public void setId_card(String id_card) {
        this.id_card = id_card;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
