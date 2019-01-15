package com.aojiexun.smartbuilding.response;

/**
 * Created by 49488 on 2018/12/14.
 */

public class PersonBean {

    /**
     * id : 7
     * id_card : 239005198909042034
     * name : 李长城
     * user_type : 1
     */

    private int id;
    private String id_card;
    private String name;
    private int user_type;

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

    public int getUser_type() {
        return user_type;
    }

    public void setUser_type(int user_type) {
        this.user_type = user_type;
    }
}
