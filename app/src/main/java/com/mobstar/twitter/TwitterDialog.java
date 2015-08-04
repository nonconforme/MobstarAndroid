package com.mobstar.twitter;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Display;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobstar.R;
import com.mobstar.twitter.TwitterApp.TwDialogListener;

public class TwitterDialog extends Dialog {

	static final float[] DIMENSIONS_LANDSCAPE = { 460, 260 };
	static final float[] DIMENSIONS_LANDSCAPE_Large = { 800, 480 };
	static final float[] DIMENSIONS_PORTRAIT = { 280, 420 };
	static final float[] DIMENSIONS_PORTRAIT_Large = { 480, 800 };
	static final FrameLayout.LayoutParams FILL = new FrameLayout.LayoutParams(
			ViewGroup.LayoutParams.MATCH_PARENT,
			ViewGroup.LayoutParams.MATCH_PARENT);
	static final int MARGIN = 4;
	static final int PADDING = 2;
	private String mUrl;
	private TwDialogListener mListener;
	private ProgressDialog mSpinner;
	private WebView mWebView;
	private LinearLayout mContent;
	private TextView mTitle;
	private boolean progressDialogRunning = false;
	Context mContext;
	Configuration config;

	public TwitterDialog(Context context, String url, TwDialogListener listener) {
		super(context);

		mUrl = url;
		mListener = listener;
		mContext = context;
		config = mContext.getResources().getConfiguration();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSpinner = new ProgressDialog(getContext());

		mSpinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mSpinner.setMessage(mContext.getString(R.string.loading) + "...");

		mContent = new LinearLayout(getContext());

		mContent.setOrientation(LinearLayout.VERTICAL);

		setUpTitle();
		setUpWebView();

		Display display = getWindow().getWindowManager().getDefaultDisplay();

		final float scale = getContext().getResources().getDisplayMetrics().density;
		float height = display.getHeight() - 50;
		float width = display.getWidth() - 50;
		float[] D_L = { height, width };
		float[] D_P = { width, height };
		float[] dimensions;

		if ((config.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
			dimensions = (display.getWidth() < display.getHeight()) ? DIMENSIONS_PORTRAIT
					: DIMENSIONS_LANDSCAPE;
		} else if ((config.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
			dimensions = (display.getWidth() < display.getHeight()) ? DIMENSIONS_PORTRAIT_Large
					: DIMENSIONS_LANDSCAPE_Large;
		} else {
			dimensions = (display.getWidth() < display.getHeight()) ? D_P : D_L;
		}

		addContentView(mContent, new FrameLayout.LayoutParams(
				(int) (dimensions[0] * scale + 0.5f), (int) (dimensions[1]
						* scale + 0.5f)));
	}

	private void setUpTitle() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		Drawable icon = getContext().getResources().getDrawable(
				R.drawable.ic_launcher);

		mTitle = new TextView(getContext());

		mTitle.setText(mContext.getString(R.string.twitter_));
		mTitle.setTextColor(Color.WHITE);
		mTitle.setTypeface(Typeface.DEFAULT_BOLD);
		mTitle.setBackgroundColor(0xFFbbd7e9);
		mTitle.setPadding(MARGIN + PADDING, MARGIN, MARGIN, MARGIN);
		mTitle.setCompoundDrawablePadding(MARGIN + PADDING);
		mTitle.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);

		mContent.addView(mTitle);
	}

	private void setUpWebView() {
		mWebView = new WebView(getContext());

		mWebView.setVerticalScrollBarEnabled(false);
		mWebView.setHorizontalScrollBarEnabled(false);
		mWebView.setWebViewClient(new TwitterWebViewClient());
		mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.setLayoutParams(FILL);
		mContent.addView(mWebView);
		mWebView.loadUrl(mUrl);
	}

	private int getScale() {
		Display display = ((WindowManager) mContext
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		int width = display.getWidth();
		Double val = new Double(width) / new Double(600);
		val = val * 100d;
		return val.intValue();
	}

	private class TwitterWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (url.startsWith(TwitterApp.CALLBACK_URL)) {
				mListener.onComplete(url, null);

				TwitterDialog.this.dismiss();

				return true;
			} else if (url.startsWith("authorize")) {
				return false;
			}
			return true;
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			mListener.onError(description);
			TwitterDialog.this.dismiss();
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			mSpinner.show();
			progressDialogRunning = true;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			String title = mWebView.getTitle();
			if (title != null && title.length() > 0) {
				mTitle.setText(title);
			}
			progressDialogRunning = false;
			mSpinner.dismiss();
		}

	}

	@Override
	protected void onStop() {
		progressDialogRunning = false;
		super.onStop();
	}

	public void onBackPressed() {
		if (!progressDialogRunning) {
			TwitterDialog.this.dismiss();

		}
		mListener.onError(mContext.getString(R.string.request_cancelled));
	}
}
