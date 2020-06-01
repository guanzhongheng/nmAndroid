package com.sznewbest.scansdkdemo.entity;

import java.io.Serializable;

public class NmResponse<T> implements Serializable {

    private static final long serialVersionUID = 5213230387175987834L;

    public int code;
    public String message;
    public Boolean success;
    public String timestamp;
    public T result;

    @Override
    public String toString() {
        return "LzyResponse{\n" +//
                "\tcode=" + code + "\n" +//
                "\tsuccess=" + success + "\n" +//
                "\tmessage='" + message + "\'\n" +////
                "\ttimestamp='" + timestamp + "\'\n" +//
                "\tresult=" + result + "\n" +//
                '}';
    }
}
