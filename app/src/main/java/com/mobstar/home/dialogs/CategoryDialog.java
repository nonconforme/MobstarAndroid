package com.mobstar.home.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import com.mobstar.R;

/**
 * Created by lipcha on 26.11.15.
 */
public class CategoryDialog extends Dialog implements View.OnClickListener {

    private BaseAdapter adapter;
    private ListView listCategory;
    private ImageButton btnClose;
    private OnChangeCategoryListener onChangeCategoryListener;

    public CategoryDialog(Context context, BaseAdapter adapter, final OnChangeCategoryListener onChangeCategoryListener) {
        super(context, R.style.DialogTheme);
        this.adapter = adapter;
        this.onChangeCategoryListener = onChangeCategoryListener;
        init();
    }

    private void init(){
        setContentView(R.layout.dialog_chooser_filter);
        listCategory   = (ListView) findViewById(R.id.list_choose_dialog);
        btnClose       = (ImageButton) findViewById(R.id.btn_close_dialog_filters);
        listCategory.setAdapter(adapter);
        btnClose.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (onChangeCategoryListener != null)
            onChangeCategoryListener.onChangeCategory(this);
    }

    public interface OnChangeCategoryListener{
        void onChangeCategory(final Dialog dialog);
    }
}
