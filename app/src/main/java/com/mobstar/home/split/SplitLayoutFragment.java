package com.mobstar.home.split;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;

import com.mobstar.R;

/**
 * Created by vasia on 06.08.15.
 */
public class SplitLayoutFragment extends Fragment implements View.OnClickListener {

    private Button btnNext, btnBack;
    private SplitActivity mSplitActivity;
    private GridView gridView;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mSplitActivity = (SplitActivity) activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View inflatedView = inflater.inflate(R.layout.fragment_split_layout, container, false);
        findViews(inflatedView);
        setListeners();
        setupAdapter();
        return inflatedView;
    }

    private void findViews(final View _view){
        btnBack = (Button) _view.findViewById(R.id.btnBack);
        btnNext = (Button) _view.findViewById(R.id.btnNext);
        gridView = (GridView) _view.findViewById(R.id.gridViewSplitLayout);
    }

    private void setListeners(){
        btnBack.setOnClickListener(this);
        btnNext.setOnClickListener(this);
    }

    private void setupAdapter(){
        SplitLayoutAdapter adapter = new SplitLayoutAdapter(getActivity());
        gridView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnBack:
                mSplitActivity.onBackPressed();
                break;
            case R.id.btnNext:
                mSplitActivity.replaceCropVideoFragment();
                break;
        }
    }
}
