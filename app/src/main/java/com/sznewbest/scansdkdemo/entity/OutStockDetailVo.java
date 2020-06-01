package com.sznewbest.scansdkdemo.entity;

public class OutStockDetailVo {
    private Long outStockDetailId;
    private String ordCode;
    private String cusCode;
    private String barCode;
    private String itemOwner;
    private String prodCgyVal;
    private String prodVarietyVal;
    private String prodColorVal;
    private String itemYbTypeVal;
    private String itemYcTypeVal;
    private Double itemLenth;
    private Double itemWidth;
    private Double prodThick;
    private Double prodWeight;
    private String prodPriceTypeVal;
    //0-未扫描 1-已扫描确认过
    private Character isOut;

    public Long getOutStockDetailId() {
        return outStockDetailId;
    }

    public void setOutStockDetailId(Long outStockDetailId) {
        this.outStockDetailId = outStockDetailId;
    }

    public String getOrdCode() {
        return ordCode;
    }

    public void setOrdCode(String ordCode) {
        this.ordCode = ordCode;
    }

    public String getCusCode() {
        return cusCode;
    }

    public void setCusCode(String cusCode) {
        this.cusCode = cusCode;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getItemOwner() {
        return itemOwner;
    }

    public void setItemOwner(String itemOwner) {
        this.itemOwner = itemOwner;
    }

    public String getProdCgyVal() {
        return prodCgyVal;
    }

    public void setProdCgyVal(String prodCgyVal) {
        this.prodCgyVal = prodCgyVal;
    }

    public String getProdVarietyVal() {
        return prodVarietyVal;
    }

    public void setProdVarietyVal(String prodVarietyVal) {
        this.prodVarietyVal = prodVarietyVal;
    }

    public String getProdColorVal() {
        return prodColorVal;
    }

    public void setProdColorVal(String prodColorVal) {
        this.prodColorVal = prodColorVal;
    }

    public String getItemYbTypeVal() {
        return itemYbTypeVal;
    }

    public void setItemYbTypeVal(String itemYbTypeVal) {
        this.itemYbTypeVal = itemYbTypeVal;
    }

    public String getItemYcTypeVal() {
        return itemYcTypeVal;
    }

    public void setItemYcTypeVal(String itemYcTypeVal) {
        this.itemYcTypeVal = itemYcTypeVal;
    }

    public Double getItemLenth() {
        return itemLenth;
    }

    public void setItemLenth(Double itemLenth) {
        this.itemLenth = itemLenth;
    }

    public Double getItemWidth() {
        return itemWidth;
    }

    public void setItemWidth(Double itemWidth) {
        this.itemWidth = itemWidth;
    }

    public Double getProdThick() {
        return prodThick;
    }

    public void setProdThick(Double prodThick) {
        this.prodThick = prodThick;
    }

    public Double getProdWeight() {
        return prodWeight;
    }

    public void setProdWeight(Double prodWeight) {
        this.prodWeight = prodWeight;
    }

    public String getProdPriceTypeVal() {
        return prodPriceTypeVal;
    }

    public void setProdPriceTypeVal(String prodPriceTypeVal) {
        this.prodPriceTypeVal = prodPriceTypeVal;
    }

    public Character getIsOut() {
        return isOut;
    }

    public void setIsOut(Character isOut) {
        this.isOut = isOut;
    }
}
