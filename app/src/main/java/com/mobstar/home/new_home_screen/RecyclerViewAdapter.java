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
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements EntryItem.OnChangeEntryListener {

    protected ArrayList<EntryPojo> arrEntryes;
    protected LayoutInflater layoutInflater;
    protected BaseActivity baseActivity;
    protected ArrayList<EntryItem> itemsList;

    public RecyclerViewAdapter(final BaseActivity activity) {
        this.arrEntryes = new ArrayList<>();
        baseActivity = activity;
        itemsList = new ArrayList<>();
        layoutInflater = LayoutInflater.from(baseActivity);
    }

    public void clearArrayEntry(){
        arrEntryes.clear();
        notifyDataSetChanged();
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
        if (position != -1 || position < arrEntryes.size())
            return arrEntryes.get(position);
        if (position == arrEntryes.size())
            return arrEntryes.get(arrEntryes.size() - 1);
        else return null;
    }

    public ArrayList<EntryPojo> getArrEntries(){
        return arrEntryes;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        final View inflatedView = layoutInflater.inflate(R.layout.row_item_entry, viewGroup, false);
        final EntryItem entryItem = new EntryItem(inflatedView);
        itemsList.add(entryItem);
        return entryItem;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder entryItem, int position) {
        ((EntryItem)entryItem).init(arrEntryes.get(position), position, baseActivity, this);
    }

    @Override
    public int getItemCount() {
        return arrEntryes.size();
    }

    @Override
    public void onRemoveEntry(int position) {
        arrEntryes.remove(position);
        notifyItemRemoved(position);
        EntryItem entryItem = getEntryAtPosition(position);
        entryItem.setPosition(-1);
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
