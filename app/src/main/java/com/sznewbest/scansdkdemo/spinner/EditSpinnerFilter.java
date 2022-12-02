package com.sznewbest.scansdkdemo.spinner;

public interface EditSpinnerFilter {
    /**
     * editText输入监听
     * @param keyword
     * @return
     */
    boolean onFilter(String keyword);
}
