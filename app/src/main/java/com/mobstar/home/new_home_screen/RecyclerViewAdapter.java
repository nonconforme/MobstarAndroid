package com.mobstar.home.new_home_screen;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobstar.BaseActivity;
import com.mobstar.R;
import com.mobstar.api.new_api_model.EntryP;
import com.mobstar.pojo.EntryPojo;

import java.util.ArrayList;

/**
 * Created by lipcha on 14.09.15.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements EntryItem.OnChangeEntryListener {

    protected ArrayList<EntryP> arrEntryes;
    protected LayoutInflater layoutInflater;
    protected BaseActivity baseActivity;
    protected ArrayList<EntryItem> itemsList;
    protected boolean isEnableSwipeAction = true;

    public RecyclerViewAdapter(final BaseActivity activity) {
        this.arrEntryes = new ArrayList<>();
        baseActivity = activity;
        itemsList = new ArrayList<>();
        layoutInflater = LayoutInflater.from(baseActivity);
    }

    public void setEnableSwipeAction(final boolean isEnable){
        isEnableSwipeAction = isEnable;
    }

    public void clearArrayEntry(){
        arrEntryes.clear();
        notifyDataSetChanged();
    }

    public void setArrEntryes(final ArrayList<EntryP> _arrEntryes){
        arrEntryes.clear();
        arrEntryes.addAll(_arrEntryes);
//        notifyDataSetChanged();

    }

    public void addArrEntries(final ArrayList<EntryP> _arrEntryes){
        arrEntryes.addAll(_arrEntryes);
//        notifyDataSetChanged();
    }

    public EntryP getEntry(int position){
        if (position == arrEntryes.size())
            return arrEntryes.get(arrEntryes.size() - 1);
        if (position != -1 || position < arrEntryes.size())
            return arrEntryes.get(position);
        else return null;
    }

    public ArrayList<EntryP> getArrEntries(){
        return arrEntryes;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        final View inflatedView = layoutInflater.inflate(R.layout.row_item_entry, viewGroup, false);
        final EntryItem entryItem = new EntryItem(inflatedView, isEnableSwipeAction);
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
        if (entryItem != null)
            entryItem.setPosition(-1);
    }

    @Override
    public void onFollowEntry(String uId, boolean isMyStar) {
        if (uId == null)
            return;
        for (int i = 0; i < arrEntryes.size(); i ++){
            if (arrEntryes.get(i).getUser().getId().equalsIgnoreCase(uId))
                arrEntryes.get(i).getUser().setIsMyStar(isMyStar);
        }
        notifyDataSetChanged();
    }

    @Override
    public void onChangeEntry(EntryP entryPojo) {
        if (entryPojo == null)
            return;
        for (int i = 0; i < itemsList.size(); i ++){
            if (itemsList.get(i).getEntryPojo().getEntry().getId().equalsIgnoreCase(entryPojo.getEntry().getId()))
                itemsList.get(i).refreshEntry(entryPojo);
        }

        for(int i = 0; i < arrEntryes.size(); i ++){
            if (arrEntryes.get(i).getUser().getId().equalsIgnoreCase(entryPojo.getUser().getId()))
                arrEntryes.set(i, entryPojo);
        }
    }

    public EntryItem getEntryAtPosition(int position){
        for (int i = 0; i < itemsList.size(); i ++){
            if (itemsList.get(i).getPos() == position)
                return itemsList.get(i);
        }
        return null;
    }
}
