package com.mobstar.home.split.position_variants;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.mobstar.R;
import com.mobstar.custom.CustomTextviewBold;
import com.mobstar.home.split.SplitActivity;

/**
 * Created by vasia on 06.08.15.
 */
public class PositionVariantsFragment extends Fragment implements View.OnClickListener {

    private CustomTextviewBold btnNext, btnBack;
    private SplitActivity mSplitActivity;
    private GridView gridView;
    private PositionVariantsAdapter mAdapter;

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
        btnBack = (CustomTextviewBold) _view.findViewById(R.id.btnBack);
        btnNext = (CustomTextviewBold) _view.findViewById(R.id.btnNext);
        gridView = (GridView) _view.findViewById(R.id.gridViewSplitLayout);
    }

    private void setListeners(){
        btnBack.setOnClickListener(this);
        btnNext.setOnClickListener(this);
    }

    private void setupAdapter(){
        mAdapter = new PositionVariantsAdapter(getActivity());
        gridView.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnBack:
                mSplitActivity.onBackPressed();
                break;
            case R.id.btnNext:
                mSplitActivity.replaceCropVideoFragment(mAdapter.getSelectedPositionVariant());
                break;
        }
    }
}
