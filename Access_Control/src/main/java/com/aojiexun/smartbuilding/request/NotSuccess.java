package com.aojiexun.smartbuilding.request;

import io.realm.RealmObject;

/**
 * Created by 49488 on 2018/12/5.
 */

public class NotSuccess extends RealmObject{
    String idCard;

    public String getCard() {
        return Card;
    }

    public void setCard(String card) {
        Card = card;
    }

    String Card;

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getTimeCreate() {
        return timeCreate;
    }

    public void setTimeCreate(String timeCreate) {
        this.timeCreate = timeCreate;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    String timeCreate;
    String image;
}
