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
import com.sznewbest.scansdkdemo.OutStockDetailActivity;
import com.sznewbest.scansdkdemo.R;
import com.sznewbest.scansdkdemo.entity.OutStockDetailVo;
import com.sznewbest.scansdkdemo.http.NmerpConnect;
import com.sznewbest.scansdkdemo.utils.StringUtils;

import java.util.List;

public class OutStockDetailAdapter extends BaseAdapter {

    private Context mContext;
    private List<OutStockDetailVo> mList;

    public OutStockDetailAdapter(){}
    public OutStockDetailAdapter(Context context, List<OutStockDetailVo> list){
        this.mContext=context;
        this.mList=list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public OutStockDetailVo getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        OutStockDetailAdapter.ViewHolder holder = null;
        if(null == view){
            view = LayoutInflater.from(mContext).inflate(R.layout.out_stock_detail_item,null);
            holder = new OutStockDetailAdapter.ViewHolder();
            holder.out_stock_detail_icon = (ImageView) view.findViewById(R.id.out_stock_detail_icon);
            holder.item_index = (TextView) view.findViewById(R.id.item_index);
            holder.item_bar_code = (TextView) view.findViewById(R.id.item_bar_code);
            holder.item_owner = (TextView) view.findViewById(R.id.item_owner);
            holder.item_variety = (TextView) view.findViewById(R.id.item_variety);
            holder.item_cgy_code = (TextView) view.findViewById(R.id.item_cgy_code);
            holder.item_weight = (TextView) view.findViewById(R.id.item_weight);
            holder.item_lw = (TextView) view.findViewById(R.id.item_lw);
            holder.item_color = (TextView) view.findViewById(R.id.item_color);
            holder.detail_item_delete = (Button) view.findViewById(R.id.detail_item_delete);
            view.setTag(holder);
        }else{
            holder = (OutStockDetailAdapter.ViewHolder) view.getTag();
        }

        final OutStockDetailVo item = getItem(i);
        int item_index = i+1;
        holder.out_stock_detail_icon.setImageResource(R.drawable.out_stock_detail);
        holder.item_index.setText("序号:"+ item_index);
        holder.item_bar_code.setText("条码号:"+ StringUtils.ifNull(item.getBarCode()));
        holder.item_owner.setText("所属人:"+ StringUtils.ifNull(item.getItemOwner()));
        holder.item_variety.setText("品种:"+StringUtils.ifNull(item.getItemVarietyValue()));
        holder.item_cgy_code.setText("类别:"+StringUtils.ifNull(item.getItemCgyCodeValue()));
        holder.item_weight.setText("重量:"+ StringUtils.ifNull(item.getItemWeight())+"kg");
        holder.item_lw.setText("长*宽:"+ StringUtils.ifNull(item.getItemLenth())+"m*"+StringUtils.ifNull(item.getItemWidth())+"m");
        holder.item_color.setText("颜色:"+StringUtils.ifNull(item.getItemColorValue()));
        holder.detail_item_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDeleteDialog(item.getBarCode(),item.getOutCode());
            }
        });
        return view;
    }

    private void confirmDeleteDialog(final String barCode,final String outCode){
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(mContext);
        normalDialog.setTitle("是否取消出库？");
        normalDialog.setMessage("点击确定将从出库单中删除此产品，产品恢复未出库状态");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        OkGo.<String>post(NmerpConnect.DELETE_BY_CODE)
                                .tag(this)
                                .params("barCode",barCode)
                                .execute(new StringCallback() {
                                    @Override
                                    public void onSuccess(Response<String> response) {
                                        Toast.makeText(mContext,"已成功取消出库。",
                                                Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent();
                                        intent.setClass(mContext, OutStockDetailActivity.class);
                                        intent.putExtra("outCode",outCode);
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
        ImageView out_stock_detail_icon;
        TextView item_index;
        TextView item_bar_code;
        TextView item_owner;
        TextView item_variety;
        TextView item_cgy_code;
        TextView item_weight;
        TextView item_lw;
        TextView item_color;
        Button detail_item_delete;
    }
}
