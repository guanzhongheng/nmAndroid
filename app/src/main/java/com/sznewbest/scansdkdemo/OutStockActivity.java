package com.sznewbest.scansdkdemo;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.sznewbest.scansdkdemo.adapter.OutStockAdapter;
import com.sznewbest.scansdkdemo.entity.OutStock;
import com.sznewbest.scansdkdemo.http.NmerpConnect;

import java.util.ArrayList;
import java.util.List;

public class OutStockActivity extends AppCompatActivity {
    private ListView listView;
    private OutStockAdapter adapter;
    private Button addNewOutStock;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_out_stock);
        // 获取界面组件
        listView = (ListView) findViewById(R.id.listview);

        // 添加列表空内容视图
        View emptyView = findViewById(R.id.empty_tv);
        listView.setEmptyView(emptyView);

        addNewOutStock = (Button) findViewById(R.id.button_add_outstock);
        addNewOutStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmDialog();
            }
        });

        refreshList();
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.i("start2","1");
        refreshList();
    }




    private void refreshList(){
        // 初始化列表
        OkGo.<String>get(NmerpConnect.OUT_STOCT_LIST)
                .tag(this)
                .execute(new StringCallback() {
                             @Override
                             public void onSuccess(Response<String> response) {

                                 List<OutStock> datas = new ArrayList<OutStock>();
                                 Gson gson = new Gson();
                                 JsonArray arry = new JsonParser().parse(response.body()).getAsJsonArray();
                                 for (JsonElement jsonElement : arry) {
                                     datas.add(gson.fromJson(jsonElement, OutStock.class));
                                 }

                                 adapter = new OutStockAdapter(OutStockActivity.this, datas);
                                 listView.setAdapter(adapter);
                             }

                             @Override
                             public void onError(Response<String> response) {
                                 super.onError(response);
                             }
                         }

                );
    }

    private void showConfirmDialog(){
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(OutStockActivity.this);
        normalDialog.setTitle("是否新增出库单？");
        normalDialog.setMessage("点击确定可创建一张新的出库单");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        OkGo.<String>post(NmerpConnect.CREATE_OUT_STOCK)
                                .tag(this)
                                .execute(new StringCallback() {
                                    @Override
                                    public void onSuccess(Response<String> response) {
                                        refreshList();
                                        Toast.makeText(OutStockActivity.this,"已成功新增一张出库单。",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
        normalDialog.setNegativeButton("关闭",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        // 显示
        normalDialog.show();
    }
}
