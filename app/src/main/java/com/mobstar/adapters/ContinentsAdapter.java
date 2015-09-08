package com.mobstar.adapters;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.mobstar.R;
import com.mobstar.pojo.ContinentsPojo;

import java.util.ArrayList;

/**
 * Created by Alexandr on 08.09.2015.
 */
public class ContinentsAdapter extends BaseAdapter {
    public final static String LOG_TAG = ContinentsAdapter.class.getName();
    private final Dialog dialog;
    private final LayoutInflater inflater;
    private final ArrayList<Integer> choosenContinents;
    private final Context context;
    private final String[] continents;

    public ContinentsAdapter(Dialog dialog, ArrayList<Integer> choosenCaontinents) {
        this.dialog = dialog;
        this.context = dialog.getContext();
        this.choosenContinents = choosenCaontinents;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        continents = context.getResources().getStringArray(R.array.continents_name);
    }
    @Override
    public int getCount() {
        return continents.length;
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
        final ViewHolder viewHolder;
        if (convertView == null) {

            convertView = inflater.inflate(R.layout.row_dialog_category, null);

            viewHolder = new ViewHolder();
            setData(viewHolder,position);

//            viewHolder.btnAll=(CustomTextviewBold)convertView.findViewById(R.id.btnAll);
//            viewHolder.textCategoryName = (TextView) convertView.findViewById(R.id.textCategoryName);
//            viewHolder.imageIcon=(ImageView)convertView.findViewById(R.id.image_icon);
//            viewHolder.llCategory=(LinearLayout)convertView.findViewById(R.id.llCategory);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }



        return convertView;
    }

    private void setData(ViewHolder viewHolder, int position) {
        ContinentsPojo continentsPojo = new ContinentsPojo(continents,position);
        switch (position){
//            case ContinentsPojo.Continents.
        }
    }

    private class ViewHolder {

    }
}
