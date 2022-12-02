package com.sznewbest.scansdkdemo.model;

public class Select {
    private String name;
    private String value;

    public Select(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public Select() {

    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


}
