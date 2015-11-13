package com.mobstar.home.search;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.mobstar.home.new_home_screen.VideoListBaseFragment;

/**
 * Created by lipcha on 13.11.15.
 */
public class SearchListFragment extends VideoListBaseFragment {

    private boolean isFirstStart = true;

    @Override
    protected void onScrolledList() {
        super.onScrolledList();
        if (isFirstStart){
            isFirstStart = false;
            return;
        }
        hideKeyboard();
    }

    private void hideKeyboard() {
        final View view = getActivity().getCurrentFocus();
        if (view != null) {
            final InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
