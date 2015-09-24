package com.mobstar.home.new_home_screen.profile;

import android.content.Context;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

/**
 * Created by lipcha on 24.09.15.
 */
public class StickyHeadersTouchListener implements RecyclerView.OnItemTouchListener {
    private final GestureDetector mTapDetector;
    private final RecyclerView mRecyclerView;
    private final StickyRecyclerHeadersDecoration mDecor;
    private StickyHeadersTouchListener.OnHeaderClickListener mOnHeaderClickListener;
    private int width;

    public StickyHeadersTouchListener(RecyclerView recyclerView, StickyRecyclerHeadersDecoration decor, Context context) {
        this.mTapDetector = new GestureDetector(recyclerView.getContext(), new StickyHeadersTouchListener.SingleTapDetector());
        this.mRecyclerView = recyclerView;
        this.mDecor = decor;
        final WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        final Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
    }

    public StickyRecyclerHeadersAdapter getAdapter() {
        if(this.mRecyclerView.getAdapter() instanceof StickyRecyclerHeadersAdapter) {
            return (StickyRecyclerHeadersAdapter)this.mRecyclerView.getAdapter();
        } else {
            throw new IllegalStateException("A RecyclerView with " + StickyHeadersTouchListener.class.getSimpleName() + " requires a " + StickyRecyclerHeadersAdapter.class.getSimpleName());
        }
    }

    public void setOnHeaderClickListener(StickyHeadersTouchListener.OnHeaderClickListener listener) {
        this.mOnHeaderClickListener = listener;
    }

    public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
        return this.mOnHeaderClickListener != null && this.mTapDetector.onTouchEvent(e);
    }

    public void onTouchEvent(RecyclerView view, MotionEvent e) {
    }

    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }

    private class SingleTapDetector extends GestureDetector.SimpleOnGestureListener {
        private SingleTapDetector() {
        }

        public boolean onSingleTapUp(MotionEvent e) {
            int position = StickyHeadersTouchListener.this.mDecor.findHeaderPositionUnder((int)e.getX(), (int)e.getY());
            if(position != -1) {
                View headerView = StickyHeadersTouchListener.this.mDecor.getHeaderView(StickyHeadersTouchListener.this.mRecyclerView, position);
                if (e.getX() < width / 2)
                    mOnHeaderClickListener.onHeaderClickRightButton();
                else mOnHeaderClickListener.onHeaderClickLeftButton();
                StickyHeadersTouchListener.this.mRecyclerView.playSoundEffect(0);
                headerView.onTouchEvent(e);
                return true;
            } else {
                return false;
            }
        }

        public boolean onDoubleTap(MotionEvent e) {
            return true;
        }
    }

    public interface OnHeaderClickListener {
        void onHeaderClickLeftButton();
        void onHeaderClickRightButton();
    }
}

