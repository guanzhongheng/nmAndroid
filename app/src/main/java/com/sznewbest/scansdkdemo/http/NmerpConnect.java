package com.sznewbest.scansdkdemo.http;

public class NmerpConnect {
    // public static final String BASE_URL = "http://47.105.143.43:8090";
//     public static final String BASE_URL = "http://182.92.73.87:8080/ls";
    // public static final String BASE_URL = "http://182.92.73.87:8080/ls";
//     public static final String BASE_URL = "http://47.105.143.43:8080/ls"; // 乔斯德瑞
     public static final String BASE_URL = "http://47.105.143.43:8079/ls"; // 淄博华海
//    public static final String BASE_URL = "http://47.105.143.43:9091/ls"; // 外部演示

    private static String append(String url){
        return BASE_URL + url;
    }

    // public static final String OUT_STOCT_LIST = append("/tOutStock/getList");
    public static final String OUT_STOCT_LIST = append("/outStock/app/outStocks");

    // public static final String CREATE_OUT_STOCK_NEW = append("/tOutStock/createNew");
    public static final String CREATE_OUT_STOCK_NEW = append("/outStock/app/create");

    // public static final String CUS_CAR_LIST = append("/tOutStock/getCarList");
    public static final String CUS_CAR_LIST = append("/outStock/app/cars");

    // public static final String CUS_INFO_LIST = append("/tOutStock/getCusInfoList");
    public static final String CUS_INFO_LIST = append("/outStock/app/cus");

    // public static final String DO_OUT_AUTO_STOCK = append("/tOutStock/doOutStockNew");
    public static final String DO_OUT_AUTO_STOCK = append("/outStock/app/addProdByCar");

    // public static final String DO_OUT_STOCK = append("/tOutStock/doOutStock");
    public static final String DO_OUT_STOCK = append("/outStock/app/addProd");

    // public static final String CREATE_OUT_STOCK = append("/tOutStock/create");

    // public static final String DELETE_BY_CODE = append("/tOutStock/deleteByCode");
    public static final String DELETE_BY_CODE = append("/outStock/app/delete");

    public static final String DELETE_PROD_BY_CODE = append("/outStock/app/delProd");

    // public static final String GET_PROD_DETAIL = append("/tOutStock/getProd");
    public static final String GET_PROD_DETAIL = append("/outStock/app/info");

    // public static final String FINISH_OUT_STOCK = append("/tOutStock/finishOutStock");
    public static final String FINISH_OUT_STOCK = append("/outStock/app/submit");

    // public static final String DETAIL_LIST = append("/tOutStock/getDetailList");
    public static final String DETAIL_LIST = append("/outStock/app/prodsByOutCode");


}
