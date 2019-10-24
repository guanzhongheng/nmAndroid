package com.sznewbest.scansdkdemo.adapter;

import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.sznewbest.scansdkdemo.CreateOutOrderActivity;

import java.util.List;

public class OutCreateOrderAdapter extends BaseAdapter {

    private List<CreateOutOrderActivity> cusList;

    public OutCreateOrderAdapter(List<CreateOutOrderActivity> cusList){
        this.cusList = cusList;
    }

    @Override
    public int getCount() {
        return cusList.size();
    }

    @Override
    public Object getItem(int i) {
        return cusList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {



        return null;
    }

    @Nullable
    @Override
    public CharSequence[] getAutofillOptions() {
        return new CharSequence[0];
    }


}
