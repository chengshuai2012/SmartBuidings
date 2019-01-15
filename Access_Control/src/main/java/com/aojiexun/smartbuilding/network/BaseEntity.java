package com.aojiexun.smartbuilding.network;

/**
 * Created by OFX002 on 2018/10/28.
 */

public class BaseEntity<T> {
    private static int SUCCESS_CODE=200;
    private int code;
    private String msg;
    private Boolean success;
    private T data;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public boolean isSuccess(){
        return getCode()==200;
    }
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }





}
