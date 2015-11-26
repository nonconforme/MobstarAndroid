package com.mobstar.adapters;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.mobstar.R;
import com.mobstar.custom.CheckableView;
import com.mobstar.pojo.ContinentsPojo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Alexandr on 08.09.2015.
 */
public class ContinentsAdapter extends BaseAdapter {
    public final static String LOG_TAG = ContinentsAdapter.class.getName();
    private final LayoutInflater inflater;
    private final List<Integer> choosenContinents;
    private final Context context;
    private final String[] continents;

    public ContinentsAdapter(final Context context, List<Integer> choosenContinents) {
        this.context = context;
        this.choosenContinents = choosenContinents;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        continents = context.getResources().getStringArray(R.array.continents_name);
        continents = new String[]{"All world"
                ,context.getResources().getString(R.string.africa)
                ,context.getResources().getString(R.string.asia)
                ,context.getResources().getString(R.string.europe)
                ,context.getResources().getString(R.string.north_america)
                ,context.getResources().getString(R.string.oceania)
                ,context.getResources().getString(R.string.south_america)
        };
        if (choosenContinents.size() == 0) {
            Collections.addAll(choosenContinents, new Integer[]{1, 2, 3, 4, 5, 6});
        }
    }
    @Override
    public int getCount() {
        return continents.length-1;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {

            convertView = inflater.inflate(R.layout.row_dialog_continents, null);

            viewHolder = new ViewHolder();
            viewHolder.checkableView = (CheckableView) convertView.findViewById(R.id.item_continent);
            viewHolder.checkableView.setVisibleChecked(true);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        setData(viewHolder,position);


        return convertView;
    }

    private void setData(final ViewHolder viewHolder, final int position) {
        final int code = position+1;
        ContinentsPojo continentsPojo = new ContinentsPojo(continents,code);
        viewHolder.checkableView.setTitle(continentsPojo.getName());
        viewHolder.checkableView.setOnlyCheck(choosenContinents.contains(code));
        viewHolder.checkableView.setLeftImageDrawable(context.getResources().getDrawable(continentsPojo.getImageResurse()));
        viewHolder.checkableView.setOnCheckedChangeListener(new CheckableView.OnCheckedChangeListener() {
            @Override
            public void onCheckedChange(CheckableView _view, boolean _checked) {
                if (choosenContinents.contains(code)){
                    choosenContinents.remove((Integer)code);
                } else {
                    choosenContinents.add(code);
                }
                Log.d(LOG_TAG,"position="+code);
                Log.d(LOG_TAG,"choosenContinents.size()="+choosenContinents.size());
            }
        });
    }

    private class ViewHolder {
        CheckableView checkableView;
    }

}
