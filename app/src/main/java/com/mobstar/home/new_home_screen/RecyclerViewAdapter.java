package com.mobstar.home.new_home_screen;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobstar.BaseActivity;
import com.mobstar.R;
import com.mobstar.pojo.EntryPojo;

import java.util.ArrayList;

/**
 * Created by lipcha on 14.09.15.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<EntryItem> implements EntryItem.OnRemoveEntryListener {

    private ArrayList<EntryPojo> arrEntryes;
    private LayoutInflater layoutInflater;
    private BaseActivity baseActivity;

    public RecyclerViewAdapter(ArrayList<EntryPojo> arrEntryes, final BaseActivity activity) {
        this.arrEntryes = arrEntryes;
        baseActivity = activity;
    }

    @Override
    public EntryItem onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (layoutInflater == null)
            layoutInflater = LayoutInflater.from(viewGroup.getContext());
        final View inflatedView = layoutInflater.inflate(R.layout.row_item_entry, viewGroup, false);
        return new EntryItem(inflatedView);
    }

    @Override
    public void onBindViewHolder(EntryItem entryItem, int position) {
        entryItem.init(arrEntryes.get(position), position, baseActivity, this);
    }

    @Override
    public int getItemCount() {
        return arrEntryes.size();
    }

    @Override
    public void onRemoveEntry(int position) {
        arrEntryes.remove(position);
//        notifyDataSetChanged();

        notifyItemRemoved(position);
    }
}
