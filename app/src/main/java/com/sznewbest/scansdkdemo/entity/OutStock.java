package com.sznewbest.scansdkdemo.entity;

import java.util.Date;

/**
 * @Project : ScanSdkDemo
 * @Description : 出库单实体类
 * @Author : wsm
 * @Iteration : 1.0
 * @Date : 2019-07-01  16:52
 * @ModificationHistory
 * Who          When          What
 * ----------   ------------- -----------------------------------
 * wsm          2019/07/01    create
 */
public class OutStock {
    private String outCode;
    private String cusName;
    private String cusCode;
    private String carNo;
    private String receiver;
    private String remark;
    private String receiverPhone;
    private String receiverAddress;
    private Long status;
    private String empCode;
    private String attachment;
    private String driver;
    private String driverPhone;
    private String statusVal;
    private String empName;
    private String createTime;
    private String updateTime;

    public String getOutCode() {
        return outCode;
    }

    public void setOutCode(String outCode) {
        this.outCode = outCode;
    }

    public String getCusName() {
        return cusName;
    }

    public void setCusName(String cusName) {
        this.cusName = cusName;
    }

    public String getCusCode() {
        return cusCode;
    }

    public void setCusCode(String cusCode) {
        this.cusCode = cusCode;
    }

    public String getCarNo() {
        return carNo;
    }

    public void setCarNo(String carNo) {
        this.carNo = carNo;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public String getEmpCode() {
        return empCode;
    }

    public void setEmpCode(String empCode) {
        this.empCode = empCode;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getDriverPhone() {
        return driverPhone;
    }

    public void setDriverPhone(String driverPhone) {
        this.driverPhone = driverPhone;
    }

    public String getStatusVal() {
        return statusVal;
    }

    public void setStatusVal(String statusVal) {
        this.statusVal = statusVal;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
