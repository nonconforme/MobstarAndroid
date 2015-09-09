package com.mobstar.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mobstar.R;
import com.mobstar.utils.Utility;

public class ResetPasswordActivity extends Activity implements OnClickListener {

	Context mContext;

	Button btnBack, btnReset;

	Typeface typefaceBtn;

	EditText editEmail;
	TextView textEmailHint;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reset_password);

		mContext = ResetPasswordActivity.this;

		InitControls();
		
		Utility.SendDataToGA("ResetPassword Screen", ResetPasswordActivity.this);
	}

	void InitControls() {

		typefaceBtn = Typeface.createFromAsset(getAssets(), "GOTHAM-BOLD.TTF");

		btnBack = (Button) findViewById(R.id.btnBack);
		btnBack.setTypeface(typefaceBtn);
		btnBack.setOnClickListener(this);

		btnReset = (Button) findViewById(R.id.btnReset);
		btnReset.setTypeface(typefaceBtn);
		btnReset.setOnClickListener(this);

		editEmail = (EditText) findViewById(R.id.editEmail);
		editEmail.setTypeface(typefaceBtn);
		editEmail.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				editEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
				textEmailHint.setVisibility(View.INVISIBLE);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});

		textEmailHint = (TextView) findViewById(R.id.textEmailHint);
		textEmailHint.setVisibility(View.INVISIBLE);
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		if (btnBack.equals(view)) {
			onBackPressed();
		} else if (btnReset.equals(view)) {
			boolean isValid = true;

			if (editEmail.getText().toString().trim().length() == 0) {
				editEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.signup_cross, 0);
				textEmailHint.setText(getString(R.string.enter_email_address));
				textEmailHint.setVisibility(View.VISIBLE);
				isValid = false;
			} else if (!Utility.IsValidEmail(editEmail)) {
				editEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.signup_cross, 0);
				textEmailHint.setText(getString(R.string.enter_valid_email_address));
				textEmailHint.setVisibility(View.VISIBLE);
				isValid = false;
			} else {
				editEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.signup_tick, 0);
				textEmailHint.setVisibility(View.INVISIBLE);
			}

			if (isValid) {
				onBackPressed();
			}
		}
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Intent intent = new Intent(mContext, LoginSocialActivity.class);
		startActivity(intent);
		finish();	
	}
}
