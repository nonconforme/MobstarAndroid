package com.mobstar.settings;

import com.mobstar.R;
import com.mobstar.R.layout;
import com.mobstar.info.report.InformationDetailActivity;
import com.mobstar.utils.Utility;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

public class LinkedAccountsActivity extends Activity {

	Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_linked_accounts);

		Utility.SendDataToGA("LinkedAccount Screen", LinkedAccountsActivity.this);
	}
}
