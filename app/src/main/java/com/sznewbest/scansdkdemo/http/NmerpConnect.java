package com.sznewbest.scansdkdemo.http;

public class NmerpConnect {
    public static final String BASE_URL = "http://47.104.243.197:8090";
//    public static final String BASE_URL = "http://192.168.101.8:8090";

    private static String append(String url){
        return BASE_URL + url;
    }

    public static final String TEST = append("/tStock/get");
    public static final String OUT_STOCT_LIST = append("/tOutStock/getList");
    public static final String CREATE_OUT_STOCK = append("/tOutStock/create");
    public static final String DETAIL_LIST = append("/tOutStock/getDetailList");
    public static final String GET_PROD_DETAIL = append("/tOutStock/getProd");
    public static final String DO_OUT_STOCK = append("/tOutStock/doOutStock");
    public static final String DELETE_BY_CODE = append("/tOutStock/deleteByCode");
    public static final String FINISH_OUT_STOCK = append("/tOutStock/finishOutStock");
}
