package com.sznewbest.scansdkdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity  {

    TextView textView_rec;
    Button button_scan;
    Button button_outbound;

    private ScanBroadcastReceiver scanBroadcastReceiver = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView_rec=(TextView)findViewById(R.id.textview_rec);
        button_scan=(Button)findViewById(R.id.button_scan);
        button_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textView_rec.setText("");
                //app发送按键广播消息方式
                Intent intentBroadcast = new Intent();
                intentBroadcast.setAction("com.zkc.keycode");
                intentBroadcast.putExtra("keyvalue", 136);
                sendBroadcast(intentBroadcast);
            }
        });

        button_outbound=(Button)findViewById(R.id.button_outbound);
        button_outbound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, OutStockActivity.class);
                startActivity(intent);

            }
        });
        //注册接扫描结果收消息广播
        if(scanBroadcastReceiver==null) {
            scanBroadcastReceiver = new ScanBroadcastReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("com.zkc.scancode");
            this.registerReceiver(scanBroadcastReceiver, intentFilter);
        }
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
            Log.i("ScanBroadcastReceiver", "ScanBroadcastReceiver code:" + decodeResult);
            textView_rec.setText(decodeResult);
        }
    }

}
