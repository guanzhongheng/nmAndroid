             package com.sznewbest.scansdkdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.sznewbest.scansdkdemo.adapter.OutStockAdapter;
import com.sznewbest.scansdkdemo.callback.DialogCallback;
import com.sznewbest.scansdkdemo.callback.JsonCallback;
import com.sznewbest.scansdkdemo.entity.NmResponse;
import com.sznewbest.scansdkdemo.entity.OutStock;
import com.sznewbest.scansdkdemo.entity.OutStockDetailVo;
import com.sznewbest.scansdkdemo.http.NmerpConnect;
import com.sznewbest.scansdkdemo.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ScanProdActivity extends AppCompatActivity {

    private Button button_back_detail;
    private Button button_scan_info;
    private Spinner car_no;
    private List<String> carList = new ArrayList<>();
    private ArrayAdapter<String> arrayScanAdapter;
    private ScanProdActivity.ScanBroadcastNewReceiver scanBroadcastNewReceiver = null;

    private String carNo;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_info);

        car_no = findViewById(R.id.car_no);
        carList.add("请选择");
        arrayScanAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, carList);
        arrayScanAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        car_no.setAdapter(arrayScanAdapter);
        //设置默认值
        car_no.setVisibility(View.VISIBLE);

        // 返回按钮
        button_back_detail = (Button) findViewById(R.id.button_back_detail);
        button_back_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ScanProdActivity.this.finish();
            }
        });

        // 扫描
        button_scan_info = findViewById(R.id.button_scan_info);
        button_scan_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //app发送按键广播消息方式
                carNo = car_no.getSelectedItem().toString();
                if("请选择".equals(carNo) || "-".equals(StringUtils.ifNull(carNo))){
                    Toast.makeText(ScanProdActivity.this,"请选择车牌号",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
               // showDialogAfterScan("7955820888572");
                Intent intentBroadcast = new Intent();
                intentBroadcast.setAction("com.zkc.keycode");
                intentBroadcast.putExtra("keydown", 136);
                intentBroadcast.putExtra("carNoInfo", carNo);
                sendBroadcast(intentBroadcast);
            }
        });

        //注册接扫描结果收消息广播
        if(scanBroadcastNewReceiver==null) {
            scanBroadcastNewReceiver = new ScanProdActivity.ScanBroadcastNewReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("com.zkc.scancode");
            this.registerReceiver(scanBroadcastNewReceiver, intentFilter);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(scanBroadcastNewReceiver);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getAjaxCarInfos();

    }

    public void getAjaxCarInfos() {
        // 调用接口获取客户列表信息
        OkGo.<NmResponse<List<String>>>get(NmerpConnect.CUS_CAR_LIST)
                .tag(this)
                .execute(new DialogCallback<NmResponse<List<String>>>(this){
                    @Override
                    public void onSuccess(Response<NmResponse<List<String>>> response) {
                        NmResponse<List<String>> nm = response.body();
                        carList.addAll(nm.result);
                    }

                    @Override
                    public void onError(Response<NmResponse<List<String>>> response) {
                        super.onError(response);
                        Toast.makeText(ScanProdActivity.this,"获取车牌号失败。",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }


    /**
     * 扫描结果广播
     */
    class ScanBroadcastNewReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String decodeResult = intent.getExtras().getString("code");
            String keyStr = "";
            if(decodeResult.contains("{")&&decodeResult.contains("}")) {
                int strStart = decodeResult.lastIndexOf("{");
                int strEnd = decodeResult.lastIndexOf("}");
                //check keycode
                if (strStart > -1 && strEnd > -1 && strEnd - strStart < 5) {
                    keyStr = decodeResult.substring(strStart + 1, strEnd);
                    decodeResult = decodeResult.substring(0, strStart);
                }
            }
            Log.i("ScanBroadcasttt", "ScanBroadcastReceiver code:" + decodeResult);
            showDialogAfterScan(decodeResult);
        }
    }

    private void showDialogAfterScan(String barCode){
        if("请选择".equals(carNo) || "-".equals(StringUtils.ifNull(carNo))){
            Toast.makeText(ScanProdActivity.this,"请选择车牌号",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        final String code = barCode;
        OkGo.<NmResponse<OutStockDetailVo>>get(NmerpConnect.GET_PROD_DETAIL)
                .tag(this)
                .params("barCode",code)
                .execute(new JsonCallback<NmResponse<OutStockDetailVo>>() {
                    @Override
                    public void onSuccess(Response<NmResponse<OutStockDetailVo>> response) {
                        NmResponse<OutStockDetailVo> nm = response.body();
                        OutStockDetailVo prodVo = nm.result;
                        if(nm.code == 200) {
                            // showScanConfirmDialog(prodVo,code,carNo);
                            ScanConfirmOut(prodVo,code,carNo);
                        }else {
//                            Toast.makeText(ScanProdActivity.this,nm.message,
//                                    Toast.LENGTH_LONG).show();
                            ScanConfirmErrorOut(nm.message);
                        }
                    }
                    @Override
                    public void onError(Response<NmResponse<OutStockDetailVo>> response) {
                        super.onError(response);
//                        Toast.makeText(ScanProdActivity.this,response.getException().getMessage(),
//                                Toast.LENGTH_SHORT).show();
                        ScanConfirmErrorOut(response.getException().getMessage());
                    }
                });
    }


    // 弹窗提示不同客户信息
    private void ScanConfirmErrorOut(String showTitle) {
        AlertDialog.Builder scDialog =
                new AlertDialog.Builder(ScanProdActivity.this);
        final View dialogView = LayoutInflater.from(ScanProdActivity.this)
                .inflate(R.layout.scan_info_confirm_dialog,null);
        scDialog.setView(dialogView);
        scDialog.setTitle(showTitle);
        scDialog.setNegativeButton("关闭",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        scDialog.show();
    }

    // 扫码后确认无误直接出库
    private void ScanConfirmOut(OutStockDetailVo prodVo,final String barCode, final String carNo) {

        if('1' == prodVo.getIsOut()){
            AlertDialog.Builder scDialog =
                    new AlertDialog.Builder(ScanProdActivity.this);
            final View dialogView = LayoutInflater.from(ScanProdActivity.this)
                    .inflate(R.layout.scan_info_confirm_dialog,null);
            scDialog.setView(dialogView);
            scDialog.setView(dialogView);
            scDialog.setTitle("此产品已存在于出库单中");
            scDialog.setNegativeButton("关闭",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            scDialog.show();
        }else{
            // 扫码即确认直接出库
            OkGo.<NmResponse<String>>post(NmerpConnect.DO_OUT_AUTO_STOCK)
                    .tag(this)
                    .params("carNo",carNo)
                    .params("barCode",barCode)
                    .execute(new JsonCallback<NmResponse<String>>(){
                        @Override
                        public void onSuccess(Response<NmResponse<String>> response) {
                            NmResponse<String> nm = response.body();
                            if(nm.code == 200) {
                                Toast.makeText(ScanProdActivity.this,"出库成功！",
                                        Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(ScanProdActivity.this,nm.message,
                                        Toast.LENGTH_SHORT).show();

                                ScanConfirmErrorOut(nm.message);
                            }
                        }
                        @Override
                        public void onError(Response<NmResponse<String>> response) {
                            super.onError(response);
//                            Toast.makeText(ScanProdActivity.this,response.getException().getMessage(),
//                                    Toast.LENGTH_SHORT).show();
                            ScanConfirmErrorOut(response.getException().getMessage());
                        }
                    });
        }
    }

    private void showScanConfirmDialog(OutStockDetailVo prodVo,final String barCode, final String carNo){
        AlertDialog.Builder scDialog =
                new AlertDialog.Builder(ScanProdActivity.this);
        final View dialogView = LayoutInflater.from(ScanProdActivity.this)
                .inflate(R.layout.scan_info_confirm_dialog,null);
        scDialog.setView(dialogView);

        TextView prod_ordCode = (TextView)dialogView.findViewById(R.id.prod_ordCode);
        prod_ordCode.setText("订单:"+ StringUtils.ifNull(prodVo.getOrdCode()));

        TextView prod_barCode = (TextView)dialogView.findViewById(R.id.prod_barCode);
        prod_barCode.setText("条码:"+StringUtils.ifNull(prodVo.getBarCode()));

        TextView prod_itemOwner = (TextView)dialogView.findViewById(R.id.prod_itemOwner);
        prod_itemOwner.setText("所属人:"+StringUtils.ifNull(prodVo.getItemOwner()));

        TextView prod_itemCgyCode = (TextView)dialogView.findViewById(R.id.prod_itemCgyCode);
        prod_itemCgyCode.setText("类别:"+StringUtils.ifNull(prodVo.getProdCgyVal()));

        TextView prod_itemVariety = (TextView)dialogView.findViewById(R.id.prod_itemVariety);
        prod_itemVariety.setText("品种:"+StringUtils.ifNull(prodVo.getProdVarietyVal()));

        TextView prod_itemColor = (TextView)dialogView.findViewById(R.id.prod_itemColor);
        prod_itemColor.setText("颜色:"+StringUtils.ifNull(prodVo.getProdColorVal()));

        TextView prod_itemYcType = (TextView)dialogView.findViewById(R.id.prod_itemYcType);
        prod_itemYcType.setText("延长米计算类型:"+StringUtils.ifNull(prodVo.getItemYcTypeVal()));

        TextView prod_itemYbType = (TextView)dialogView.findViewById(R.id.prod_itemYbType);
        prod_itemYbType.setText("压边类型:"+StringUtils.ifNull(prodVo.getItemYbTypeVal()));

        TextView prod_itemWidth = (TextView)dialogView.findViewById(R.id.prod_itemWidth);
        prod_itemWidth.setText("宽度:"+StringUtils.ifNull(prodVo.getItemWidth())+"m");

        TextView prod_itemThick = (TextView)dialogView.findViewById(R.id.prod_itemThick);
        prod_itemThick.setText("厚度:"+StringUtils.ifNull(prodVo.getProdThick())+"cm");

        TextView prod_itemLenth = (TextView)dialogView.findViewById(R.id.prod_itemLenth);
        prod_itemLenth.setText("长度:"+StringUtils.ifNull(prodVo.getItemLenth())+"m");

        TextView prod_itemWeight = (TextView)dialogView.findViewById(R.id.prod_itemWeight);
        prod_itemWeight.setText("重量:"+StringUtils.ifNull(prodVo.getProdWeight())+"kg");

        TextView prod_itemSq = (TextView)dialogView.findViewById(R.id.prod_itemSq);
        prod_itemSq.setText("面积:"+StringUtils.ifNull(prodVo.getItemLenth() * prodVo.getItemWidth())+"㎡");

        TextView prod_itemUnit = (TextView)dialogView.findViewById(R.id.prod_itemUnit);
        prod_itemUnit.setText("计价方式:"+StringUtils.ifNull(prodVo.getProdPriceTypeVal()));


        Button button = (Button)dialogView.findViewById(R.id.prod_confirm);
        button.setText("此产品已出库");
        button.setClickable(false);
        if('1' == prodVo.getIsOut()){
            scDialog.setTitle("此产品已存在于出库单中");
            scDialog.setNegativeButton("关闭",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
        }else{
            scDialog.setTitle("是否确认出库？");
            scDialog.setPositiveButton("确定",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, int which) {
                            OkGo.<NmResponse<String>>post(NmerpConnect.DO_OUT_AUTO_STOCK)
                                    .tag(this)
                                    .params("carNo",carNo)
                                    .params("barCode",barCode)
                                    .execute(new JsonCallback<NmResponse<String>>(){
                                        @Override
                                        public void onSuccess(Response<NmResponse<String>> response) {
                                            NmResponse<String> nm = response.body();
                                            if(nm.code == 200) {
                                                Toast.makeText(ScanProdActivity.this,"出库成功！",
                                                        Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                            }else {
                                                Toast.makeText(ScanProdActivity.this,nm.message,
                                                        Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                        }
                                        @Override
                                        public void onError(Response<NmResponse<String>> response) {
                                            super.onError(response);
                                            Toast.makeText(ScanProdActivity.this,response.getException().getMessage(),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        }
                    });
            scDialog.setNegativeButton("关闭",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
        }
        scDialog.show();
    }

}
