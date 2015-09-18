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
public class RecyclerViewAdapter extends RecyclerView.Adapter<EntryItem> implements EntryItem.OnChangeEntryListener {

    private ArrayList<EntryPojo> arrEntryes;
    private LayoutInflater layoutInflater;
    private BaseActivity baseActivity;
    private ArrayList<EntryItem> itemsList;

    public RecyclerViewAdapter(final BaseActivity activity) {
        this.arrEntryes = new ArrayList<>();
        baseActivity = activity;
        itemsList = new ArrayList<>();
    }

    public void setArrEntryes(final ArrayList<EntryPojo> _arrEntryes){
        arrEntryes.clear();
        arrEntryes.addAll(_arrEntryes);
//        notifyDataSetChanged();

    }

    public void addArrEntries(final ArrayList<EntryPojo> _arrEntryes){
        arrEntryes.addAll(_arrEntryes);
//        notifyDataSetChanged();
    }

    public EntryPojo getEntry(int position){
        if (position > arrEntryes.size())
            return arrEntryes.get(arrEntryes.size() - 1);
        return arrEntryes.get(position);
    }

    @Override
    public EntryItem onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (layoutInflater == null)
            layoutInflater = LayoutInflater.from(viewGroup.getContext());
        final View inflatedView = layoutInflater.inflate(R.layout.row_item_entry, viewGroup, false);
        final EntryItem entryItem = new EntryItem(inflatedView);
        itemsList.add(entryItem);
        return entryItem;
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
        notifyItemRemoved(position);
    }

    @Override
    public void onFollowEntry(String uId, String isMyStar) {
        if (uId == null)
            return;
        for (int i = 0; i < arrEntryes.size(); i ++){
            if (arrEntryes.get(i).getUserID().equalsIgnoreCase(uId))
                arrEntryes.get(i).setIsMyStar(isMyStar);
        }
        notifyDataSetChanged();
    }

    public EntryItem getEntryAtPosition(int position){
        for (int i = 0; i < itemsList.size(); i ++){
            if (itemsList.get(i).getPos() == position)
                return itemsList.get(i);
        }
        return null;
    }
}
