package com.sznewbest.scansdkdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
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
                Intent intentBroadcast = new Intent();
                intentBroadcast.setAction("com.zkc.keycode");
                intentBroadcast.putExtra("keyvalue", 136);
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
        OkGo.<String>post(NmerpConnect.CUS_CAR_LIST)
                .tag(this)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {

                        List<String> datas = new ArrayList<>();
                        Gson gson = new Gson();
                        JsonArray arry = new JsonParser().parse(response.body()).getAsJsonArray();
                        for (JsonElement jsonElement : arry) {
                            carList.add(jsonElement.toString().replaceAll("\"",""));
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
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
        OkGo.<String>get(NmerpConnect.GET_PROD_DETAIL)
                .tag(this)
                .params("barCode",code)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Gson gson = new Gson();
                        OutStockDetailVo prodVo = gson.fromJson(response.body(),OutStockDetailVo.class);
                        if(prodVo.getStockId() == null || "".equals(prodVo.getStockId())){
                            Toast.makeText(ScanProdActivity.this,"该条码在系统中不存在，请确认后再试。",
                                    Toast.LENGTH_LONG).show();
                        }else{
                            showScanConfirmDialog(prodVo,code,carNo);
                        }

                    }
                });
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
        prod_itemCgyCode.setText("类别:"+StringUtils.ifNull(prodVo.getItemCgyCodeValue()));

        TextView prod_itemVariety = (TextView)dialogView.findViewById(R.id.prod_itemVariety);
        prod_itemVariety.setText("品种:"+StringUtils.ifNull(prodVo.getItemVarietyValue()));

        TextView prod_itemColor = (TextView)dialogView.findViewById(R.id.prod_itemColor);
        prod_itemColor.setText("颜色:"+StringUtils.ifNull(prodVo.getItemColorValue()));

        TextView prod_itemYcType = (TextView)dialogView.findViewById(R.id.prod_itemYcType);
        prod_itemYcType.setText("延长米计算类型:"+StringUtils.ifNull(prodVo.getItemYcTypeValue()));

        TextView prod_itemYbType = (TextView)dialogView.findViewById(R.id.prod_itemYbType);
        prod_itemYbType.setText("压边类型:"+StringUtils.ifNull(prodVo.getItemYbTypeValue()));

        TextView prod_itemWidth = (TextView)dialogView.findViewById(R.id.prod_itemWidth);
        prod_itemWidth.setText("宽度:"+StringUtils.ifNull(prodVo.getItemWidth())+"m");

        TextView prod_itemThick = (TextView)dialogView.findViewById(R.id.prod_itemThick);
        prod_itemThick.setText("厚度:"+StringUtils.ifNull(prodVo.getItemThick())+"cm");

        TextView prod_itemLenth = (TextView)dialogView.findViewById(R.id.prod_itemLenth);
        prod_itemLenth.setText("长度:"+StringUtils.ifNull(prodVo.getItemLenth())+"m");

        TextView prod_itemWeight = (TextView)dialogView.findViewById(R.id.prod_itemWeight);
        prod_itemWeight.setText("重量:"+StringUtils.ifNull(prodVo.getItemWeight())+"kg");

        TextView prod_itemSq = (TextView)dialogView.findViewById(R.id.prod_itemSq);
        prod_itemSq.setText("面积:"+StringUtils.ifNull(prodVo.getItemSq())+"㎡");

        TextView prod_itemUnit = (TextView)dialogView.findViewById(R.id.prod_itemUnit);
        prod_itemUnit.setText("单位:"+StringUtils.ifNull(prodVo.getItemUnitValue()));


        Button button = (Button)dialogView.findViewById(R.id.prod_confirm);
        button.setText("此产品已出库");
        button.setClickable(false);
        if('1' == prodVo.getIsOut()){
            scDialog.setTitle("此产品已包含在出库单内");
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
                            OkGo.<String>post(NmerpConnect.DO_OUT_AUTO_STOCK)
                                    .tag(this)
                                    .params("carNo",carNo)
                                    .params("barCode",barCode)
                                    .execute(new StringCallback() {
                                        @Override
                                        public void onSuccess(Response<String> response) {
                                            String res = response.body().toString();
                                            if("-1".equals(res)){
                                                Toast.makeText(ScanProdActivity.this,"产品不存在出库单关联！",
                                                        Toast.LENGTH_SHORT).show();
                                                return;
                                            }else if("0".equals(res)){
                                                Toast.makeText(ScanProdActivity.this,"出库单已经存在该产品！",
                                                        Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            Toast.makeText(ScanProdActivity.this,"出库成功！",
                                                    Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
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