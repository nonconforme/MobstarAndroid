package com.mobstar.custom;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobstar.R;
import com.tokenautocomplete.TokenCompleteTextView;

public class TagView extends TokenCompleteTextView {
	public TagView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected View getViewForObject(Object object) {
		String string = (String) object;

		LayoutInflater l = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		LinearLayout view = (LinearLayout) l.inflate(R.layout.layout_tags, (ViewGroup) TagView.this.getParent(), false);
		((TextView) view.findViewById(R.id.textTag)).setText(string);

		return view;
	}

	
	@Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        //Override normal multiline text handling of enter/done and force a done button
		BaseInputConnection connection = new BaseInputConnection(this, false);
        return connection;
    }
	
	@Override
	protected Object defaultObject(String completionText) {
		return null;
	}

}
