package com.sznewbest.scansdkdemo.utils;

public class StringUtils {
    public static String ifNull(String str){
        if(null == str || "".equals(str)){
            return "-";
        }else{
            return str;
        }
    }

    public static String ifNull(Double d){
        if(null == d ){
            return "-";
        }else{
            return String.valueOf(d);
        }
    }
}
