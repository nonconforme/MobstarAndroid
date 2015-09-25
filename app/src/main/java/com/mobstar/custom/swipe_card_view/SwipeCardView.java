package com.mobstar.custom.swipe_card_view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;

import java.util.Random;

/**
 * Created by lipcha on 13.09.15.
 */
public class SwipeCardView extends FrameLayout {

    private int mActivePointerId = -1;
    private final Random mRandom = new Random();
    private final Rect boundsRect = new Rect();
    private final Rect childRect = new Rect();
    private final Matrix mMatrix = new Matrix();
    private GestureDetector mGestureDetector;
    private int mFlingSlop;
    private Orientations.Orientation mOrientation;
    private float mLastTouchX;
    private float mLastTouchY;
    private View mTopView;
    private int mTouchSlop;
    private int mGravity;
    private boolean mDragging;
    private OnSwipeDismissListener onSwipeDismissListener;


    private View swipeLeftViewIndicator;
    private View swipeRightViewIndicator;

    public SwipeCardView(Context context) {
        super(context);
        this.setOrientation(Orientations.Orientation.Disordered);
        this.setGravity(17);
        this.init();
    }

    public SwipeCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initFromXml(attrs);
        this.init();
    }

    public SwipeCardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.initFromXml(attrs);
        this.init();
    }

    private void init() {
        ViewConfiguration viewConfiguration = ViewConfiguration.get(this.getContext());
        this.mFlingSlop = viewConfiguration.getScaledMinimumFlingVelocity();
        this.mTouchSlop = viewConfiguration.getScaledTouchSlop();
        this.mGestureDetector = new GestureDetector(this.getContext(), new GestureListener());

    }

    private void initFromXml(AttributeSet attr) {
//        TypedArray a = this.getContext().obtainStyledAttributes(attr, com.andtinder.R.styleable.CardContainer);
//        this.setGravity(a.getInteger(0, 17));
//        int orientation = a.getInteger(1, 1);
//        this.setOrientation(Orientations.Orientation.fromIndex(orientation));
//        a.recycle();
    }

    private void setupTopView(final View _view){
        this.mTopView = _view;
        this.requestLayout();
    }

    public void setOnSwipeDismissListener(final OnSwipeDismissListener _onSwipeDismissListener){
        onSwipeDismissListener = _onSwipeDismissListener;
    }

    public void clearStack() {
        this.removeAllViewsInLayout();
        this.mTopView = null;
    }

    public View getTopView() {
        return mTopView;
    }

    public void resetTopView(){
        if (mTopView == null)
            return;
        mTopView.setX(0f);
        mTopView.setY(0f);
        mTopView.setRotation(0f);
        mTopView.setAlpha(1.0f);
        if (swipeLeftViewIndicator != null)
            swipeLeftViewIndicator.setVisibility(GONE);
        if (swipeRightViewIndicator != null)
            swipeRightViewIndicator.setVisibility(GONE);
    }

    public void setOrientation(Orientations.Orientation orientation) {
        if(orientation == null) {
            throw new NullPointerException("Orientation may not be null");
        } else {
            if(this.mOrientation != orientation) {
                this.mOrientation = orientation;
                int i;
                View child;
                if(orientation == Orientations.Orientation.Disordered) {
                    for(i = 0; i < this.getChildCount(); ++i) {
                        child = this.getChildAt(i);
                        child.setRotation(this.getDisorderedRotation());
                    }
                } else {
                    for(i = 0; i < this.getChildCount(); ++i) {
                        child = this.getChildAt(i);
                        child.setRotation(0.0F);
                    }
                }

                this.requestLayout();
            }

        }
    }

    private float getDisorderedRotation() {
        return (float) Math.toDegrees(this.mRandom.nextGaussian() * 0.04908738521234052D);
    }

    @Override
    public void addView(View child) {
        if (getChildCount() > 0) {
            throw new IllegalStateException("SwipeCardView can host only one direct child");
        }
        Log.d("tagAdd", "add 1");
        super.addView(child);
    }

    @Override
    public void addView(View child, int index) {
        if (getChildCount() > 0) {
            throw new IllegalStateException("SwipeCardView can host only one direct child");
        }
        Log.d("tagAdd", "add 2");
        super.addView(child, index);
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        if (getChildCount() > 0) {
            throw new IllegalStateException("SwipeCardView can host only one direct child");
        }
        Log.d("tagAdd", "add 3");
        setupTopView(child);
        super.addView(child, params);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (getChildCount() > 0) {
            throw new IllegalStateException("SwipeCardView can host only one direct child");
        }
        Log.d("tagAdd", "add 4");
        setupTopView(child);
        super.addView(child, index, params);
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        for(int i = 0; i < this.getChildCount(); ++i) {
            this.boundsRect.set(0, 0, this.getWidth(), this.getHeight());
            View view = this.getChildAt(i);
            int w = view.getMeasuredWidth();
            int h = view.getMeasuredHeight();
            Gravity.apply(this.mGravity, w, h, this.boundsRect, this.childRect);
            view.layout(this.childRect.left, this.childRect.top, this.childRect.right, this.childRect.bottom);
        }

    }

    public boolean onTouchEvent(MotionEvent event) {
        if(this.mTopView == null) {
            return false;
        } else if(this.mGestureDetector.onTouchEvent(event)) {
            return true;
        } else {
            Log.d("Touch Event", MotionEvent.actionToString(event.getActionMasked()) + " ");
            int pointerIndex;
            float x;
            float y;
            switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    this.mTopView.getHitRect(this.childRect);
                    pointerIndex = event.getActionIndex();
                    x = event.getX(pointerIndex);
                    y = event.getY(pointerIndex);
                    if(!this.childRect.contains((int)x, (int)y)) {
                        return false;
                    }

                    this.mLastTouchX = x;
                    this.mLastTouchY = y;
                    this.mActivePointerId = event.getPointerId(pointerIndex);
                    float[] points = new float[]{x - (float)this.mTopView.getLeft(), y - (float)this.mTopView.getTop()};
                    this.mTopView.getMatrix().invert(this.mMatrix);
                    this.mMatrix.mapPoints(points);
                    this.mTopView.setPivotX(points[0]);
                    this.mTopView.setPivotY(points[1]);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    if(!this.mDragging) {
                        return true;
                    }

                    this.mDragging = false;
                    this.mActivePointerId = -1;
                    ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(this.mTopView, new PropertyValuesHolder[]{PropertyValuesHolder.ofFloat("translationX", new float[]{0.0F}), PropertyValuesHolder.ofFloat("translationY", new float[]{0.0F}), PropertyValuesHolder.ofFloat("rotation", new float[]{0.0F}), PropertyValuesHolder.ofFloat("pivotX", new float[]{(float) this.mTopView.getWidth() / 2.0F}), PropertyValuesHolder.ofFloat("pivotY", new float[]{(float) this.mTopView.getHeight() / 2.0F})}).setDuration(250L);
                    animator.setInterpolator(new AccelerateInterpolator());
                    animator.start();

                    if (swipeRightViewIndicator != null)
                        swipeRightViewIndicator.setVisibility(GONE);
                    if (swipeLeftViewIndicator != null)
                        swipeLeftViewIndicator.setVisibility(GONE);
                    break;
                case MotionEvent.ACTION_MOVE:
                    pointerIndex = event.findPointerIndex(this.mActivePointerId);
                    x = event.getX(pointerIndex);
                    y = event.getY(pointerIndex);
                    float dx = x - this.mLastTouchX;
                    float dy = y - this.mLastTouchY;
                    if(Math.abs(dx) > (float)this.mTouchSlop || Math.abs(dy) > (float)this.mTouchSlop) {
                        this.mDragging = true;
                    }

                    if(!this.mDragging) {
                        return true;
                    }
                    getParent().requestDisallowInterceptTouchEvent(true);
                    this.mTopView.setTranslationX(this.mTopView.getTranslationX() + dx);
                    this.mTopView.setTranslationY(this.mTopView.getTranslationY() + dy);
                    this.mTopView.setRotation(40.0F * this.mTopView.getTranslationX() / ((float) this.getWidth() / 2.0F));
                    this.mLastTouchX = x;
                    this.mLastTouchY = y;
                    final float targetX = mTopView.getX();
                    setVoting(targetX);

                case MotionEvent.ACTION_OUTSIDE:
                case MotionEvent.ACTION_POINTER_DOWN:
                default:
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    pointerIndex = event.getActionIndex();
                    int pointerId = event.getPointerId(pointerIndex);
                    if(pointerId == this.mActivePointerId) {
                        int newPointerIndex = pointerIndex == 0?1:0;
                        this.mLastTouchX = event.getX(newPointerIndex);
                        this.mLastTouchY = event.getY(newPointerIndex);
                        this.mActivePointerId = event.getPointerId(newPointerIndex);
                    }
            }

            return true;
        }
    }

    private void setVoting(float targetX){
        float alpha = 1;
        if (targetX < 80) {
            alpha = Math.abs(0.01f * targetX);
        }
        if (targetX > 0.0F){
            if (swipeRightViewIndicator != null) {
                swipeRightViewIndicator.setVisibility(VISIBLE);
                swipeRightViewIndicator.setAlpha(alpha);
            }
            if (swipeLeftViewIndicator != null)
                swipeLeftViewIndicator.setVisibility(GONE);
        }else {
            if (swipeLeftViewIndicator != null) {
                swipeLeftViewIndicator.setVisibility(VISIBLE);
                swipeLeftViewIndicator.setAlpha(alpha);
            }
            if (swipeRightViewIndicator != null)
                swipeRightViewIndicator.setVisibility(GONE);
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent event) {
        if(this.mTopView == null) {
            return false;
        } else if(this.mGestureDetector.onTouchEvent(event)) {
            return true;
        } else {
            int pointerIndex;
            float x;
            float y;
            switch(event.getActionMasked()) {
                case 0:
                    this.mTopView.getHitRect(this.childRect);
                    pointerIndex = event.getActionIndex();
                    x = event.getX(pointerIndex);
                    y = event.getY(pointerIndex);
                    if(!this.childRect.contains((int)x, (int)y)) {
                        return false;
                    }

                    this.mLastTouchX = x;
                    this.mLastTouchY = y;
                    this.mActivePointerId = event.getPointerId(pointerIndex);
                    break;
                case 2:
                    pointerIndex = event.findPointerIndex(this.mActivePointerId);
                    x = event.getX(pointerIndex);
                    y = event.getY(pointerIndex);
                    if(Math.abs(x - this.mLastTouchX) > (float)this.mTouchSlop || Math.abs(y - this.mLastTouchY) > (float)this.mTouchSlop) {
                        float[] points = new float[]{x - (float)this.mTopView.getLeft(), y - (float)this.mTopView.getTop()};
                        this.mTopView.getMatrix().invert(this.mMatrix);
                        this.mMatrix.mapPoints(points);
                        this.mTopView.setPivotX(points[0]);
                        this.mTopView.setPivotY(points[1]);
                        return true;
                    }
            }

            return false;
        }
    }
    public void setGravity(int gravity) {
        this.mGravity = gravity;
    }

    public void setSwipeLeftViewIndicator(View swipeLeftViewIndicator) {
        this.swipeLeftViewIndicator = swipeLeftViewIndicator;
    }

    public void setSwipeRightViewIndicator(View swipeRightViewIndicator) {
        this.swipeRightViewIndicator = swipeRightViewIndicator;
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private GestureListener() {
        }

        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            final View topCard = mTopView;
            float dx = e2.getX() - e1.getX();
            if(Math.abs(dx) > (float)mTouchSlop && Math.abs(velocityX) > Math.abs(velocityY) && Math.abs(velocityX) > (float)(mFlingSlop * 3)) {
                float targetX = topCard.getX();
                float targetY = topCard.getY();
                long duration = 0L;
                boundsRect.set(0 - topCard.getWidth() - 100, 0 - topCard.getHeight() - 100, getWidth() + 100, getHeight() + 100);

                while(boundsRect.contains((int)targetX, (int)targetY)) {
                    targetX += velocityX / 10.0F;
                    targetY += velocityY / 10.0F;
                    duration += 100L;
                }

                duration = Math.min(500L, duration);
//                mTopView = getChildAt(getChildCount() - 2);

                final float finalTargetX = targetX;
                topCard.animate().setDuration(duration).alpha(0.75F).setInterpolator(new LinearInterpolator()).x(targetX).y(targetY).rotation(Math.copySign(45.0F, velocityX)).setListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
//                        removeViewInLayout(topCard);
                        invalidate();
                        if(onSwipeDismissListener != null) {
                            if(finalTargetX > 0.0F) {
                                onSwipeDismissListener.onSwipeRight();
                            } else {
                                onSwipeDismissListener.onSwipeLeft();
                            }

                        }

                    }

                    public void onAnimationCancel(Animator animation) {
                        this.onAnimationEnd(animation);
                    }
                });
                return true;
            } else {
                return false;
            }
        }
    }

    public interface OnSwipeDismissListener{
        void onSwipeLeft();
        void onSwipeRight();
    }
}