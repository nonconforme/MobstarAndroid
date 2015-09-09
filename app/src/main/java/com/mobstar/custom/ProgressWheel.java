package com.mobstar.custom;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.widget.CompoundButton;

import com.mobstar.R;

/**
 * A two-state button that indicates whether some related content is pinned (the
 * checked state) or unpinned (the unchecked state), and the download progress
 * for this content.
 * <p/>
 * See <a href=
 * "http://developer.android.com/design/building-blocks/progress.html#custom-indicators"
 * >Android Design: Progress &amp; Activity</a> for more details on this custom
 * indicator.
 * <p/>
 * The example on the website is also the default visual implementation that is
 * provided. By default the button is not clickable (by the user). If you want
 * the user to be able to control the state, use
 * {@link android.view.View#setClickable(boolean)}.
 * 
 * @see android.view.View#setClickable(boolean)
 * @see android.view.View#setFocusable(boolean)
 */
public class ProgressWheel extends CompoundButton {

	/** The maximum progress. Defaults to 100. */
	private int mMax = 100;
	/** The current progress. Defaults to 0. */
	private int mProgress = 0;
	/** The paint for the circle. */
	private Paint mCirclePaint;
	/** True if the view is animating. Defaults to false. */
	private boolean mAnimating = false;
	/** Animation speed. Defaults to 1. */
	private int mAnimationSpeed = 1;
	/** Delay between animation frames. Defaults to 50. */
	private int mAnimationDelay = 50;
	/** Width of the animation strip. Defaults to 6. */
	private int mAnimationStripWidth = 6;
	/** Internal variable to track animation state. */
	private int mAnimationProgress = 0;

	/**
	 * The paint to show the progress.
	 * 
	 * @see #mProgress
	 */
	private Paint mProgressPaint;
	private Rect mTempRect = new Rect();
	private RectF mTempRectF = new RectF();
	private int mInnerSize;

	private Handler mAnimationHandler = new Handler() {
		/**
		 * This is the code that will increment the progress variable and so
		 * spin the wheel
		 */
		@Override
		public void handleMessage(Message msg) {
			if (mAnimating) {
				invalidate();
				mAnimationProgress += mAnimationSpeed;
				if (mAnimationProgress > mMax) {
					mAnimationProgress = mProgress;
				}
				mAnimationHandler.sendEmptyMessageDelayed(0, mAnimationDelay);
			}
		}
	};

	public ProgressWheel(Context context) {
		this(context, null);
	}

	public ProgressWheel(Context context, AttributeSet attrs) {
		this(context, attrs, R.attr.progressButtonStyle);
	}

	public ProgressWheel(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs, defStyle);
	}

	/**
	 * Initialise the {@link ProgressButton}.
	 * 
	 * @param context
	 *            the application environment
	 * @param attrs
	 *            Attribute Set provided
	 * @param defStyle
	 *            The style resource to pull from
	 */
	private void init(Context context, AttributeSet attrs, int defStyle) {
		// Attribute initialization
		final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ProgressButton, defStyle, R.style.ProgressButton_Pin_Compat);
		final Resources res = getResources();

		mProgress = a.getInteger(R.styleable.ProgressButton_progress, mProgress);
		mMax = a.getInteger(R.styleable.ProgressButton_max, mMax);

		int circleColor = res.getColor(R.color.gray_color);
		circleColor = a.getColor(R.styleable.ProgressButton_circleColor, circleColor);
		int progressColor = res.getColor(R.color.yellow_color);
		progressColor = a.getColor(R.styleable.ProgressButton_progressColor, progressColor);

		mInnerSize = res.getDimensionPixelSize(R.dimen.progress_inner_size);
		mInnerSize = a.getDimensionPixelSize(R.styleable.ProgressButton_innerSize, mInnerSize);

		setChecked(a.getBoolean(R.styleable.ProgressButton_pinned, false));
		setClickable(a.getBoolean(R.styleable.ProgressButton_android_clickable, false));
		setFocusable(a.getBoolean(R.styleable.ProgressButton_android_focusable, false));
		setBackgroundDrawable(a.getDrawable(R.styleable.ProgressButton_android_background));

		mAnimating = a.getBoolean(R.styleable.ProgressButton_animating, mAnimating);
		mAnimationSpeed = a.getInteger(R.styleable.ProgressButton_animationSpeed, mAnimationSpeed);
		mAnimationDelay = a.getInteger(R.styleable.ProgressButton_animationDelay, mAnimationDelay);
		mAnimationStripWidth = a.getInteger(R.styleable.ProgressButton_animationStripWidth, mAnimationStripWidth);

		a.recycle();

		mCirclePaint = new Paint();
		mCirclePaint.setColor(circleColor);
		mCirclePaint.setAntiAlias(true);

		mProgressPaint = new Paint();
		mProgressPaint.setColor(progressColor);
		mProgressPaint.setAntiAlias(true);

		if (mAnimating) {
			startAnimating();
		}
	}

	/** Returns the maximum progress value. */
	public int getMax() {
		return mMax;
	}

	/** Sets the maximum progress value. Defaults to 100. */
	public void setMax(int max) {
		if (max <= 0 || max < mProgress) {
			throw new IllegalArgumentException(String.format("Max (%d) must be > 0 and >= %d", max, mProgress));
		}
		mMax = max;
		invalidate();
	}

	/** Returns the current progress from 0 to max. */
	public int getProgress() {
		return mProgress;
	}

	/**
	 * Sets the current progress (must be between 0 and max).
	 * 
	 * @see #setMax(int)
	 */
	public void setProgress(int progress) {
		if (progress > mMax || progress < 0) {
			throw new IllegalArgumentException(String.format("Progress (%d) must be between %d and %d", progress, 0, mMax));
		}
		mProgress = progress;
		invalidate();
	}

	/**
	 * Sets the current progress and maximum progress value, both of which must
	 * be valid values.
	 * 
	 * @see #setMax(int)
	 * @see #setProgress(int)
	 */
	public void setProgressAndMax(int progress, int max) {
		if (progress > max || progress < 0) {
			throw new IllegalArgumentException(String.format("Progress (%d) must be between %d and %d", progress, 0, max));
		} else if (max <= 0) {
			throw new IllegalArgumentException(String.format("Max (%d) must be > 0", max));
		}

		mProgress = progress;
		mMax = max;
		invalidate();
	}

	/** Get the color used to display the progress level. */
	public int getProgressColor() {
		return mProgressPaint.getColor();
	}

	/** Sets the color used to display the progress level. */
	public void setProgressColor(int progressColor) {
		mProgressPaint.setColor(progressColor);
		invalidate();
	}

	/** Get the color used to display the progress background. */
	public int getCircleColor() {
		return mCirclePaint.getColor();
	}

	/** Sets the color used to display the progress background. */
	public void setCircleColor(int circleColor) {
		mCirclePaint.setColor(circleColor);
		invalidate();
	}

	public int getInnerSize() {
		return mInnerSize;
	}

	public void setInnerSize(int innerSize) {
		mInnerSize = innerSize;
		invalidate();
	}

	/**
	 * Get whether the button is pinned or not. Equivalent to
	 * {@link CompoundButton#isChecked()}
	 */
	public boolean isPinned() {
		return isChecked();
	}

	/**
	 * Set whether the button is pinned or not. Equivalent to
	 * {@link CompoundButton#setChecked(boolean)}
	 */
	public void setPinned(boolean pinned) {
		setChecked(pinned);
	}

	/** Returns true if the button is animating. */
	public boolean isAnimating() {
		return mAnimating;
	}

	/** Get the animation speed. */
	public int getAnimationSpeed() {
		return mAnimationSpeed;
	}

	/** Get the animation delay. */
	public int getAnimationDelay() {
		return mAnimationDelay;
	}

	/** Get the width of the animation strip. */
	public int getAnimationStripWidth() {
		return mAnimationStripWidth;
	}

	/**
	 * Set the animation speed. This speed controls what progress we jump by in
	 * the animation.
	 */
	public void setAnimationSpeed(int animationSpeed) {
		mAnimationSpeed = animationSpeed;
	}

	/**
	 * Set the delay of the animation. This controls the duration between each
	 * frame.
	 */
	public void setAnimationDelay(int animationDelay) {
		mAnimationDelay = animationDelay;
	}

	/** Set the width of the animation strip. */
	public void setAnimationStripWidth(int animationStripWidth) {
		mAnimationStripWidth = animationStripWidth;
	}

	/** Start animating the button. */
	public void startAnimating() {
		if (!mAnimating) {
			mAnimating = true;
			mAnimationProgress = mProgress;
			mAnimationHandler.sendEmptyMessage(0);
		}
	}

	/** Stop animating the button. */
	public void stopAnimating() {
		mAnimating = false;
		mAnimationProgress = mProgress;
		mAnimationHandler.removeMessages(0);
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		mTempRectF.set(-0.5f, -0.5f, mInnerSize + 0.5f, mInnerSize + 0.5f);
		mTempRectF.offset((getWidth() - mInnerSize) / 2, (getHeight() - mInnerSize) / 2);

		canvas.drawArc(mTempRectF, 0, 360, true, mCirclePaint);
		canvas.drawArc(mTempRectF, -90, 360 * mProgress / mMax, true, mProgressPaint);

		if (mAnimating) {
			canvas.drawArc(mTempRectF, -90 + (360 * mAnimationProgress / mMax), mAnimationStripWidth, true, mProgressPaint);
		}

	}

	@Override
	public Parcelable onSaveInstanceState() {
		Parcelable superState = super.onSaveInstanceState();
		if (isSaveEnabled()) {
			SavedState ss = new SavedState(superState);
			ss.mMax = mMax;
			ss.mProgress = mProgress;
			return ss;
		}
		return superState;
	}

	@Override
	public void onRestoreInstanceState(Parcelable state) {
		if (!(state instanceof SavedState)) {
			super.onRestoreInstanceState(state);
			return;
		}

		SavedState ss = (SavedState) state;
		super.onRestoreInstanceState(ss.getSuperState());

		mMax = ss.mMax;
		mProgress = ss.mProgress;
	}

	/**
	 * A {@link android.os.Parcelable} representing the {@link ProgressButton}'s
	 * state.
	 */
	public static class SavedState extends BaseSavedState {
		public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
			@Override
			public SavedState createFromParcel(Parcel parcel) {
				return new SavedState(parcel);
			}

			@Override
			public SavedState[] newArray(int size) {
				return new SavedState[size];
			}
		};
		private int mProgress;
		private int mMax;

		public SavedState(Parcelable superState) {
			super(superState);
		}

		private SavedState(Parcel in) {
			super(in);
			mProgress = in.readInt();
			mMax = in.readInt();
		}

		@Override
		public void writeToParcel(Parcel out, int flags) {
			super.writeToParcel(out, flags);
			out.writeInt(mProgress);
			out.writeInt(mMax);
		}
	}
	
}
