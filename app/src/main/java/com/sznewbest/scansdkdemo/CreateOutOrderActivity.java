package com.sznewbest.scansdkdemo;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.sznewbest.scansdkdemo.adapter.OutStockAdapter;
import com.sznewbest.scansdkdemo.callback.DialogCallback;
import com.sznewbest.scansdkdemo.entity.CusManageVo;
import com.sznewbest.scansdkdemo.entity.NmResponse;
import com.sznewbest.scansdkdemo.entity.OutStock;
import com.sznewbest.scansdkdemo.http.NmerpConnect;
import com.sznewbest.scansdkdemo.model.Select;
import com.sznewbest.scansdkdemo.spinner.SearchSpinner;
import com.sznewbest.scansdkdemo.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateOutOrderActivity extends AppCompatActivity {

    private Button button_back_order;
    private Button button_save_order;
    private Spinner cusCode;
    private SearchSpinner searchSpinner;
    private EditText carNo;

    private List<Select> cusList = new ArrayList<>();
    private Map<String, String> cusMap = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_outorder);

        searchSpinner = findViewById(R.id.cus_code);
        searchSpinner.setRightImageResource(R.drawable.ic_expand_more_black);
        searchSpinner.setHint("请选择客户");


//        cusCode = findViewById(R.id.cus_code);
//        cusList.add("请选择");
//        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, cusList);
//        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        cusCode.setAdapter(arrayAdapter);
//        //设置默认值
//        cusCode.setVisibility(View.VISIBLE);

        // 返回按钮
        button_back_order = (Button) findViewById(R.id.button_back_order);
        button_back_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateOutOrderActivity.this.finish();
            }
        });

        // 保存按钮
        button_save_order = (Button) findViewById(R.id.button_save_order);
        button_save_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createOutPlanForCusCodeAndCarNo();
            }
        });

    }

    ;

    private void createOutPlanForCusCodeAndCarNo() {
        // 获取关联用户
//        cusCode = findViewById(R.id.cus_code);
        Select spinnerText = searchSpinner.getSelect();
        // 获取车辆牌号信息
        carNo = findViewById(R.id.car_no);
        if(spinnerText == null) {
            Toast.makeText(CreateOutOrderActivity.this,"请选择列正确的客户信息。",
                    Toast.LENGTH_SHORT).show();
            return;
        }
//        String cusName = cusCode.getSelectedItem().toString();
        String cusName = spinnerText.getName();
        String carNoInfo = carNo.getText().toString();
        if("请选择".equals(cusName)){
            Toast.makeText(CreateOutOrderActivity.this,"请选择客户。",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if("-".equals(StringUtils.ifNull(cusName)) || "-".equals(StringUtils.ifNull(carNoInfo)) ){
            Toast.makeText(CreateOutOrderActivity.this,"请填入正确的信息。",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("carNo",carNoInfo);
        params.put("cusCode",spinnerText.getValue());
        showConfirmForSave(params);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getAjaxCusInfos();

    }

    public void getAjaxCusInfos() {
        // 调用接口获取客户列表信息
        OkGo.<NmResponse<List<CusManageVo>>>get(NmerpConnect.CUS_INFO_LIST)
                .tag(this)
                .execute(new DialogCallback<NmResponse<List<CusManageVo>>>(this){
                    @Override
                    public void onSuccess(Response<NmResponse<List<CusManageVo>>> response) {
                        NmResponse<List<CusManageVo>> nm = response.body();
                        if (nm.result != null && nm.result.size() > 0) {
                            for (CusManageVo c : nm.result) {
                                if (cusMap.get(c.getCusName()) == null) {
                                    Select st = new Select();
                                    st.setValue(c.getCusCode());
                                    st.setName(c.getCusName());
                                    cusList.add(st);

                                    searchSpinner.setItemData(cusList);
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(Response<NmResponse<List<CusManageVo>>> response) {
                        super.onError(response);
                        Toast.makeText(CreateOutOrderActivity.this,"出库单创建失败。",
                                Toast.LENGTH_SHORT).show();
                    }
                });
        // 调用接口获取客户列表信息

//        OkGo.<String>post(NmerpConnect.CUS_INFO_LIST)
//                .tag(this)
//                .execute(new StringCallback() {
//                    @Override
//                    public void onSuccess(Response<String> response) {
//
//                        List<CusManageVo> datas = new ArrayList<CusManageVo>();
//                        Gson gson = new Gson();
//                        JsonArray arry = new JsonParser().parse(response.body()).getAsJsonArray();
//                        for (JsonElement jsonElement : arry) {
//                            datas.add(gson.fromJson(jsonElement, CusManageVo.class));
//                        }
//                        if (datas != null && datas.size() > 0) {
//                            for (CusManageVo c : datas) {
//                                if (cusMap.get(c.getCusName()) == null) {
//                                    cusList.add(c.getCusName());
//                                    cusMap.put(c.getCusName(), c.getCusCode());
//                                }
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onError(Response<String> response) {
//                        super.onError(response);
//                        Toast.makeText(CreateOutOrderActivity.this,"出库单创建失败。",
//                                Toast.LENGTH_SHORT).show();
//                    }
//
//                });

    }


    //新建出库单
    private void showConfirmForSave(final Map<String, String> params){
        OkGo.<String>post(NmerpConnect.CREATE_OUT_STOCK_NEW)
                .tag(this)
                .params(params)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String result = response.body();
                        if("-1".equals(result)){
                            Toast.makeText(CreateOutOrderActivity.this,"对应信息的出库单已经存在。",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        CreateOutOrderActivity.this.finish();

                    }
                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                    }
                });

    }

}
