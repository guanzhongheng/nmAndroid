package com.sznewbest.scansdkdemo.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.sznewbest.scansdkdemo.OutStockActivity;
import com.sznewbest.scansdkdemo.OutStockDetailActivity;
import com.sznewbest.scansdkdemo.R;
import com.sznewbest.scansdkdemo.entity.OutStock;
import com.sznewbest.scansdkdemo.http.NmerpConnect;

import java.util.List;

/**
 * @Project : ScanSdkDemo
 * @Description : 出库单适配器
 * @Author : wsm
 * @Iteration : 1.0
 * @Date : 2019-07-01  17:01
 * @ModificationHistory Who          When          What
 * ----------   ------------- -----------------------------------
 * wsm          2019/07/01    create
 */
public class OutStockAdapter extends BaseAdapter {

    private Context mContext;
    private List<OutStock> mList;

    public OutStockAdapter(){}
    public OutStockAdapter(Context context, List<OutStock> list){
        this.mContext=context;
        this.mList=list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public OutStock getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if(null == view){
            view = LayoutInflater.from(mContext).inflate(R.layout.out_stock_item,null);
            holder = new ViewHolder();
            holder.out_stock_icon = (ImageView) view.findViewById(R.id.out_stock_icon);
            holder.out_stock_id = (TextView) view.findViewById(R.id.out_stock_id);
            holder.to_detail = (Button) view.findViewById(R.id.to_detail);
            holder.delete_out_stock = (Button) view.findViewById(R.id.delete_out_stock);
            holder.finish_out_stock = (Button) view.findViewById(R.id.finish_out_stock);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }

        OutStock order = getItem(i);
        final String outCode = order.getOutCode();
        holder.out_stock_icon.setImageResource(R.drawable.out_stock);
        holder.out_stock_id.setText("出库单"+outCode);
        holder.to_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(view.getContext(), OutStockDetailActivity.class);
                intent.putExtra("outCode",outCode);
                view.getContext().startActivity(intent);
            }
        });

        holder.delete_out_stock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDeleteDialog(outCode);
            }
        });
        holder.finish_out_stock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmFinishDialog(outCode);
            }
        });
        return view;
    }

    private void confirmFinishDialog(final String outCode){
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(mContext);
        normalDialog.setTitle("是否完成出库单？");
        normalDialog.setMessage("点击确定将结束本出库单");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        OkGo.<String>post(NmerpConnect.FINISH_OUT_STOCK)
                                .tag(this)
                                .params("outCode",outCode)
                                .execute(new StringCallback() {
                                    @Override
                                    public void onSuccess(Response<String> response) {
                                        Toast.makeText(mContext,"已成功完结出库单。",
                                                Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent();
                                        intent.setClass(mContext, OutStockActivity.class);
                                        mContext.startActivity(intent);
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

    private void confirmDeleteDialog(final String outCode){
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(mContext);
        normalDialog.setTitle("是否删除出库单？");
        normalDialog.setMessage("点击确定将删除本出库单，同时已本出库单内已出库的产品将全部变味未出库并且需要重新扫描");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        OkGo.<String>post(NmerpConnect.DELETE_BY_CODE)
                                .tag(this)
                                .params("outCode",outCode)
                                .execute(new StringCallback() {
                                    @Override
                                    public void onSuccess(Response<String> response) {
                                        Toast.makeText(mContext,"已成功删除出库单。",
                                                Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent();
                                        intent.setClass(mContext, OutStockActivity.class);
                                        mContext.startActivity(intent);
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
    private class ViewHolder{
        ImageView out_stock_icon;
        TextView out_stock_id;
        Button delete_out_stock;
        Button finish_out_stock;
        Button to_detail;
    }
}
