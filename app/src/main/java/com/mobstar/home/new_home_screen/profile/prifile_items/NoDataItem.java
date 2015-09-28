package com.mobstar.home.new_home_screen.profile.prifile_items;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mobstar.R;

/**
 * Created by lipcha on 23.09.15.
 */
public class NoDataItem  extends RecyclerView.ViewHolder {
    private TextView textNoData;

    public NoDataItem(View itemView) {
        super(itemView);
        textNoData = (TextView) itemView.findViewById(R.id.textNoData);
    }

    public void init(final Context context){
        textNoData.setText(context.getString(R.string.there_are_no_entries_yet));
    }
}
