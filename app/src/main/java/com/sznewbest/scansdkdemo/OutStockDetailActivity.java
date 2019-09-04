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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.sznewbest.scansdkdemo.adapter.OutStockDetailAdapter;
import com.sznewbest.scansdkdemo.entity.OutStockDetailVo;
import com.sznewbest.scansdkdemo.http.NmerpConnect;
import com.sznewbest.scansdkdemo.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class OutStockDetailActivity extends AppCompatActivity {
    private ListView detail_listview;
    private OutStockDetailAdapter adapter;
    private Button button_detail_back;
    private Button button_detail_scan;
    private String outCode;
    private OutStockDetailActivity.ScanBroadcastReceiver scanBroadcastReceiver = null;
    private List<OutStockDetailVo> datas = null;
    @Override
    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_out_stock_detail);
        Intent intent = getIntent();
        outCode = intent.getStringExtra("outCode");
        // 获取界面组件
        detail_listview = (ListView) findViewById(R.id.detail_listview);

        // 添加列表空内容视图
        View emptyView = findViewById(R.id.detail_empty_tv);
        detail_listview.setEmptyView(emptyView);

        button_detail_back = (Button) findViewById(R.id.button_detail_back);
        button_detail_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                OutStockDetailActivity.this.finish();
            }
        });

        button_detail_scan = (Button) findViewById(R.id.button_detail_scan);
        button_detail_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //app发送按键广播消息方式
                Intent intentBroadcast = new Intent();
                intentBroadcast.setAction("com.zkc.keycode");
                intentBroadcast.putExtra("keyvalue", 136);
                sendBroadcast(intentBroadcast);
            }
        });

        //注册接扫描结果收消息广播
        if(scanBroadcastReceiver==null) {
            scanBroadcastReceiver = new OutStockDetailActivity.ScanBroadcastReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("com.zkc.scancode");
            this.registerReceiver(scanBroadcastReceiver, intentFilter);
        }

        refreshList(outCode);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(scanBroadcastReceiver);
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.i("start2","1");
        refreshList(outCode);
    }

    private void refreshList(String outCode){
        // 初始化列表
        OkGo.<String>get(NmerpConnect.DETAIL_LIST)
                .tag(this)
                .params("outCode",outCode)
                .execute(new StringCallback() {
                             @Override
                             public void onSuccess(Response<String> response) {

                                 datas = new ArrayList<OutStockDetailVo>();
                                 Gson gson = new Gson();
                                 JsonArray arry = new JsonParser().parse(response.body()).getAsJsonArray();
                                 for (JsonElement jsonElement : arry) {
                                     datas.add(gson.fromJson(jsonElement, OutStockDetailVo.class));
                                 }

                                 adapter = new OutStockDetailAdapter(OutStockDetailActivity.this, datas);
                                 detail_listview.setAdapter(adapter);
                             }

                             @Override
                             public void onError(Response<String> response) {
                                 super.onError(response);
                             }
                         }

                );
    }

    /**
     * 扫描结果广播
     */
    class ScanBroadcastReceiver extends BroadcastReceiver {
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
                            Toast.makeText(OutStockDetailActivity.this,"该条码在系统中不存在，请确认后再试。",
                                    Toast.LENGTH_LONG).show();
                        }else{
                            if(datas == null || datas.size() == 0 ){
                                showScanConfirmDialog(prodVo,code);
                            }else{
                                if(prodVo.getOrdCode() != null && prodVo.getOrdCode().equals(datas.get(0).getOrdCode())){
                                    showScanConfirmDialog(prodVo,code);
                                }else{
                                    Toast.makeText(OutStockDetailActivity.this,"该产品与当前出库单中的其他产品不属于同一个订单！请检查后再试。",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        }

                    }
                });
    }

    private void showScanConfirmDialog(OutStockDetailVo prodVo,final String barCode){
        AlertDialog.Builder scDialog =
                new AlertDialog.Builder(OutStockDetailActivity.this);
        final View dialogView = LayoutInflater.from(OutStockDetailActivity.this)
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
                            OkGo.<String>post(NmerpConnect.DO_OUT_STOCK)
                                    .tag(this)
                                    .params("outCode",outCode)
                                    .params("barCode",barCode)
                                    .execute(new StringCallback() {
                                        @Override
                                        public void onSuccess(Response<String> response) {
                                            Toast.makeText(OutStockDetailActivity.this,"出库成功！",
                                                    Toast.LENGTH_SHORT).show();
                                            refreshList(outCode);
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
