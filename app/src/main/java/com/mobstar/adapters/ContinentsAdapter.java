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

/**
 * Created by Alexandr on 08.09.2015.
 */
public class ContinentsAdapter extends BaseAdapter implements CheckableView.OnCheckedChangeListener {
    public final static String LOG_TAG = ContinentsAdapter.class.getName();
    private final Dialog dialog;
    private final LayoutInflater inflater;
    private final ArrayList<Integer> choosenContinents;
    private final Context context;
    private final String[] continents;

    public ContinentsAdapter(Dialog dialog, ArrayList<Integer> choosenContinents) {
        this.dialog = dialog;
        this.context = dialog.getContext();
        this.choosenContinents = choosenContinents;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        continents = context.getResources().getStringArray(R.array.continents_name);
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
        ContinentsPojo continentsPojo = new ContinentsPojo(continents,position);
        viewHolder.checkableView.setTitle(continentsPojo.getName());
        viewHolder.checkableView.setOnlyCheck(choosenContinents.contains(position));
        viewHolder.checkableView.setLeftImageDrawable(context.getResources().getDrawable(continentsPojo.getImageResurse()));
        viewHolder.checkableView.setOnCheckedChangeListener(new CheckableView.OnCheckedChangeListener() {
            @Override
            public void onCheckedChange(CheckableView _view, boolean _checked) {
                if (choosenContinents.contains(position)){
                    choosenContinents.remove((Integer)position);
                } else {
                    choosenContinents.add(position);
                }
                Log.d(LOG_TAG,"position="+position);
                Log.d(LOG_TAG,"choosenContinents.size()="+choosenContinents.size());
            }
        });
    }

    @Override
    public void onCheckedChange(CheckableView _view, boolean _checked) {

    }

    private class ViewHolder {
        CheckableView checkableView;
    }

}
