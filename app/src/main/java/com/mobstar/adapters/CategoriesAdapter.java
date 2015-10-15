package com.mobstar.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.mobstar.R;
import com.mobstar.custom.CheckableView;
import com.mobstar.pojo.CategoryPojo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Alexandr on 10.09.2015.
 */
public class CategoriesAdapter extends BaseAdapter {
    public final static String LOG_TAG = CategoriesAdapter.class.getName();
    private final LayoutInflater inflater;
    private final ArrayList<Integer> choosenCategories;
    private final ArrayList<CategoryPojo> allCategories;
    private final Context context;
    private final int size;

    public CategoriesAdapter(Context context,ArrayList<CategoryPojo> allCategories, ArrayList<Integer> choosenCategories) {
        this.context = context;
        this.allCategories = allCategories;
        this.choosenCategories = choosenCategories;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (choosenCategories.size() == 0) {
            allChoose();
        }

        size = context.getResources().getDimensionPixelOffset(R.dimen.ic_size_category);
        Log.d(LOG_TAG,"allCategories.size="+allCategories.size());
        Log.d(LOG_TAG,"choosenCategories.size="+this.choosenCategories.size());
    }
    @Override
    public int getCount() {
        return allCategories.size()+1;
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
        final CheckableView checkableView =  viewHolder.checkableView;
        if (position == 0){
         setupAllCategoryItem(checkableView);
        }
        else {
           setupCategorieItems(checkableView, position);
        }
    }

    private void setupAllCategoryItem(final CheckableView checkableView){
        checkableView.setVisibleChecked(false);
        checkableView.getTvTitle().setTextColor(context.getResources().getColor(android.R.color.black));
        checkableView.setTitle(context.getResources().getString(R.string.all));
        checkableView.setIsVisibleShadow(false);
        checkableView.getIvLeftImage().getLayoutParams().height= ViewGroup.LayoutParams.WRAP_CONTENT;
        checkableView.getIvLeftImage().getLayoutParams().width= ViewGroup.LayoutParams.WRAP_CONTENT;
        Picasso.with(context).load(R.drawable.icn_btn_all)
                .into(checkableView.getIvLeftImage());
        checkableView.setMainBackground(context.getResources().getDrawable(R.drawable.oval_white_button_background));
        checkableView.setOnCheckedChangeListener(null);
        checkableView.setCustomOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allChoose();
                Log.d(LOG_TAG, "click on ALL");
                notifyDataSetChanged();
            }
        });
    }

    private void setupCategorieItems(final CheckableView checkableView, final int position){
        checkableView.getTvTitle().setTextColor(context.getResources().getColor(R.color.white_color));
        final CategoryPojo categoryObj = allCategories.get(position - 1);
        if (categoryObj.getCategoryName() != null && categoryObj.getCategoryName().length() > 0) {

            checkableView.setTitle(categoryObj.getCategoryName());
        }
        checkableView.getIvLeftImage().getLayoutParams().height= size;
        checkableView.getIvLeftImage().getLayoutParams().width= size;
        if (categoryObj.getCategoryDescription() != null && categoryObj.getCategoryDescription().length() > 0) {
            Picasso.with(context).load(categoryObj.getCategoryDescription())
                    .placeholder(R.drawable.ic_pic_small)
                    .into(checkableView.getIvLeftImage());
        } else {
            Picasso.with(context).load(R.drawable.ic_pic_small)
                    .into(checkableView.getIvLeftImage());
        }
        //set background
        if(!categoryObj.getCategoryActive()){
            checkableView.setVisibleChecked(false);
            checkableView.setMainBackground(context.getResources().getDrawable(R.drawable.btn_coming_soon));
            checkableView.setIsVisibleShadow(false);
            checkableView.setCustomOnClickListener(null);
        }
        else {
            checkableView.setOnlyCheck(choosenCategories.contains(Integer.parseInt(categoryObj.getID())));
            checkableView.setMainBackground(context.getResources().getDrawable(R.drawable.yellow_btn));
            checkableView.setOnCheckedChangeListener(new CheckableView.OnCheckedChangeListener() {
                @Override
                public void onCheckedChange(CheckableView _view, boolean _checked) {
                    if (choosenCategories.contains(Integer.parseInt(categoryObj.getID()))) {
                        choosenCategories.remove((Integer)Integer.parseInt(categoryObj.getID()));
                    } else {
                        choosenCategories.add(Integer.parseInt(categoryObj.getID()));
                    }
                    Log.d(LOG_TAG, "position=" + categoryObj.getID());
                    Log.d(LOG_TAG, "choosenContinents.size()=" + choosenCategories.size());
                }
            });
        }
    }



    private void allChoose() {
        choosenCategories.clear();
        for (CategoryPojo allCategory : allCategories) {
            if(allCategory.getCategoryActive()){
                choosenCategories.add(Integer.parseInt(allCategory.getID()));
            }
        }
    }

    private class ViewHolder {
        CheckableView checkableView;
    }
}
