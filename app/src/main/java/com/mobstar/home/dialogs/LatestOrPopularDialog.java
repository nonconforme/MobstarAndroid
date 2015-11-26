package com.mobstar.home.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.ImageButton;

import com.mobstar.R;
import com.mobstar.custom.CustomTextviewBold;

/**
 * Created by lipcha on 26.11.15.
 */
public class LatestOrPopularDialog extends Dialog implements View.OnClickListener {

    private CustomTextviewBold btnLatest, btnPopular;
    private ImageButton btnClose;
    private OnSelectLatestOrPopularListener onSelectlatestrPopularListener;

    public LatestOrPopularDialog(Context context, OnSelectLatestOrPopularListener onSelectlatestrPopularListener) {
        super(context, R.style.DialogTheme);
        this.onSelectlatestrPopularListener = onSelectlatestrPopularListener;
        init();
    }

    private void init(){
        setContentView(R.layout.dialog_latest_popular);
        setCancelable(true);
        setupI();
    }

    private void setupI(){
        btnClose    = (ImageButton) findViewById(R.id.btnClose);
        btnLatest   = (CustomTextviewBold) findViewById(R.id.btnLatest);
        btnPopular  = (CustomTextviewBold) findViewById(R.id.btnPopular);

        btnClose.setOnClickListener(this);
        btnLatest.setOnClickListener(this);
        btnPopular.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnClose:

                break;
            case R.id.btnLatest:
                if (onSelectlatestrPopularListener != null)
                    onSelectlatestrPopularListener.onSelectLatest();
                break;
            case R.id.btnPopular:
                if (onSelectlatestrPopularListener != null)
                    onSelectlatestrPopularListener.onSelectPopular();
                break;
        }
        dismiss();
    }

    public interface OnSelectLatestOrPopularListener {
        void onSelectLatest();
        void onSelectPopular();
    }
}
