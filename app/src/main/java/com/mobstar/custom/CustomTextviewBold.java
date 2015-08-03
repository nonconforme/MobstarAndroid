package com.mobstar.custom;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class CustomTextviewBold extends TextView {

	

	public CustomTextviewBold(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		
	}

	public CustomTextviewBold(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		

	}

	public CustomTextviewBold(Context context) {
		super(context);
		

	}

	@Override
	public void setTypeface(Typeface tf, int style) {
		// TODO Auto-generated method stub
//
		if (!isInEditMode()) {

			Typeface typeface;

			typeface = Typeface.createFromAsset(getContext().getAssets(), "GOTHAM-BOLD.TTF");

			super.setTypeface(typeface, style);
		}

	}

}
