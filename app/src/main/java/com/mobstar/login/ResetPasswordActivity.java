package com.mobstar.login;

import android.app.Activity;
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
import com.mobstar.api.ConnectCallback;
import com.mobstar.api.new_api_call.AuthCall;
import com.mobstar.api.new_api_model.response.SuccessResponse;
import com.mobstar.api.responce.*;
import com.mobstar.utils.Utility;

public class ResetPasswordActivity extends Activity implements OnClickListener, TextWatcher {

	private Button btnBack, btnReset;
	private Typeface typefaceBtn;
	private EditText editEmail;
	private TextView textEmailHint;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reset_password);
		initControls();
		Utility.SendDataToGA("ResetPassword Screen", ResetPasswordActivity.this);
	}

	private void initControls() {
		findViews();
		setListeners();
		setTypeface();
		textEmailHint.setVisibility(View.INVISIBLE);
	}

	private void findViews(){
		btnBack         = (Button) findViewById(R.id.btnBack);
		btnReset        = (Button) findViewById(R.id.btnReset);
		editEmail       = (EditText) findViewById(R.id.editEmail);
		textEmailHint   = (TextView) findViewById(R.id.textEmailHint);
	}

	private void setListeners(){
		btnBack.setOnClickListener(this);
		btnReset.setOnClickListener(this);
		editEmail.addTextChangedListener(this);
	}

	private void setTypeface(){
		typefaceBtn = Typeface.createFromAsset(getAssets(), "GOTHAM-BOLD.TTF");
		btnBack.setTypeface(typefaceBtn);
		btnReset.setTypeface(typefaceBtn);
		editEmail.setTypeface(typefaceBtn);
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		editEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		textEmailHint.setVisibility(View.INVISIBLE);
	}

	@Override
	public void afterTextChanged(Editable s) {

	}

	@Override
	public void onClick(View view) {
		switch (view.getId()){
			case R.id.btnBack:
				onBackPressed();
				break;
			case R.id.btnReset:
				resetPassword();
				break;
		}
	}

	private void resetPassword(){
		if (isValidMail()) {
			resetPasswordRequest(editEmail.getText().toString().trim());
		}
	}

	public boolean isValidMail(){
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
		return isValid;
	}

	private void resetPasswordRequest(final String email){
		Utility.ShowProgressDialog(this, getString(R.string.reset_password));
		AuthCall.postForgotPassword(this, email, new ConnectCallback<SuccessResponse>() {
			@Override
			public void onSuccess(SuccessResponse object) {
				Utility.HideDialog(ResetPasswordActivity.this);
				onBackPressed();
			}

			@Override
			public void onFailure(String error) {
				Utility.HideDialog(ResetPasswordActivity.this);
			}

			@Override
			public void onServerError(com.mobstar.api.responce.Error error) {
				Utility.HideDialog(ResetPasswordActivity.this);
			}
		});
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		startLoginSocialActivity();
	}

	private void startLoginSocialActivity(){
		Intent intent = new Intent(this, LoginSocialActivity.class);
		startActivity(intent);
		finish();
	}

}
