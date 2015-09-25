package com.mobstar.custom.recycler_view;

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
    private int currentTopItem = -1;
    private int oldTopItem = 0;
    private int currentPage = 1;
    private int limitPage = 1;

    private LinearLayoutManager mLinearLayoutManager;
    private boolean delFlag = false;

    public void reset(){
        previousTotal = 0;
        currentPage = 1;
        oldTopItem = 0;
        limitPage = 1;
        loading = true;
    }

    public void existNextPage() {
        limitPage++;
    }

    public void setLinearLayoutManager(final LinearLayoutManager _linearLayoutManager) {
        mLinearLayoutManager = _linearLayoutManager;
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        if (mLinearLayoutManager == null || recyclerView.getAdapter().getItemCount() == 0)
            return;
        verifyLoadingNewPage(recyclerView);
        onChangeState(recyclerView, newState);
    }

    private void verifyLoadingNewPage(final RecyclerView recyclerView) {
        int totalItemCount = mLinearLayoutManager.getItemCount();
        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
            }
        }
        if (!loading && (totalItemCount - recyclerView.getChildCount()) <= (mLinearLayoutManager.findFirstVisibleItemPosition() + VISIBLE_THRESHOLD)) {
            if (currentPage >= limitPage)
                return;
            currentPage++;
            onLoadMore(currentPage);
            loading = true;
        }
    }

    private void onChangeState(RecyclerView recyclerView, int newState) {
        switch (newState) {
            case RecyclerView.SCROLL_STATE_DRAGGING:

                break;
            case RecyclerView.SCROLL_STATE_IDLE:
                final int topVisiblePosition = getTopVisiblePosition(recyclerView, mLinearLayoutManager);
                if (topVisiblePosition == -1)
                    return;
                if (currentTopItem != topVisiblePosition || delFlag) {
                    delFlag = false;
                    oldTopItem = currentTopItem;
                    onLoadNewFile(topVisiblePosition, oldTopItem);
                    currentTopItem = topVisiblePosition;
                }
                break;
        }
    }

    public static int getTopVisiblePosition(final RecyclerView recyclerView, LinearLayoutManager linearLayoutManager) {
        int firstVisiblePosition = linearLayoutManager.findFirstVisibleItemPosition();
        final View firstVisibleItem = linearLayoutManager.findViewByPosition(firstVisiblePosition);
        if (firstVisibleItem == null)
            return 0;
        if (Math.abs(firstVisibleItem.getTop()) > firstVisibleItem.getHeight() / 2 && firstVisiblePosition < recyclerView.getAdapter().getItemCount() - 2) {
            firstVisiblePosition++;
        }
        return firstVisiblePosition;
    }

    public void onFailedLoading(){
        loading = false;
        previousTotal--;
        if (currentPage > 2)
            currentPage--;
    }

    public abstract void onLoadMore(int current_page);

    public abstract void onLoadNewFile(int currentPosition, int oldPosition);

    public void setDelFlag(boolean b) {
        delFlag = b;

    }
}
