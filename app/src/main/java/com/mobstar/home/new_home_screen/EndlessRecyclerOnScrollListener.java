package com.mobstar.home.new_home_screen;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by lipcha on 16.09.15.
 */
public abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {

    private static final int VISIBLE_THRESHOLD = 5;

    private int previousTotal = 0;
    private boolean loading = true;
    private int currentTopItem = 0;
    private int oldTopItem = 0;
    private int currentPage = 0;

    private LinearLayoutManager mLinearLayoutManager;

    public EndlessRecyclerOnScrollListener(LinearLayoutManager linearLayoutManager) {
        this.mLinearLayoutManager = linearLayoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        int totalItemCount = mLinearLayoutManager.getItemCount();
        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
            }
        }
        if (!loading && (totalItemCount - recyclerView.getChildCount()) <= (mLinearLayoutManager.findFirstVisibleItemPosition() + VISIBLE_THRESHOLD)) {
            currentPage++;
            onLoadMore(currentPage);
            loading = true;
        }
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);

        switch (newState){
            case RecyclerView.SCROLL_STATE_DRAGGING:

                break;
            case RecyclerView.SCROLL_STATE_IDLE:
                final int topVisiblePosition = getTopVisiblePosition(recyclerView, mLinearLayoutManager);
                if (currentTopItem != topVisiblePosition) {
                    oldTopItem = currentTopItem;
                    onLoadNewFile(topVisiblePosition, oldTopItem);
                    currentTopItem = topVisiblePosition;
                }
                break;
        }

    }

    public static int getTopVisiblePosition(final RecyclerView recyclerView, LinearLayoutManager linearLayoutManager){
        int firstVisiblePosition = linearLayoutManager.findFirstVisibleItemPosition();
        final View firstVisibleItem = linearLayoutManager.findViewByPosition(firstVisiblePosition);
        if (Math.abs(firstVisibleItem.getTop()) > firstVisibleItem.getHeight() / 2 && firstVisiblePosition < recyclerView.getAdapter().getItemCount() - 2){
            firstVisiblePosition++;
        }
        return firstVisiblePosition;
    }

    public abstract void onLoadMore(int current_page);

    public abstract void onLoadNewFile(int currentPosition, int oldPosition);
}
