package com.mobstar.stararea;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobstar.R;
import com.mobstar.pojo.EntryPojo;
import com.mobstar.utils.Utility;

public class EditInformationActivity extends Activity {

	ArrayList<String> arrayTags = new ArrayList<String>();
	Context mContext;

	TagListAdapter tagListAdapter;
	ListView listTags;

	EditText editTitle, editTag;
	TextView textTitleHint;
	ImageView btnAddTag;

	Typeface typefaceBtn;
	Button btnBack, btnFinish;

	SharedPreferences preferences;

	EntryPojo entryPojo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_upload_file);

		mContext = EditInformationActivity.this;

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			entryPojo = (EntryPojo) extras.getSerializable("entry");
		}
		preferences = getSharedPreferences("mobstar_pref", Activity.MODE_PRIVATE);

		InitControls();
		
		Utility.SendDataToGA("EditInformation Screen", EditInformationActivity.this);

	}

	void InitControls() {

		typefaceBtn = Typeface.createFromAsset(getAssets(), "GOTHAM-BOLD.TTF");

		listTags = (ListView) findViewById(R.id.listTags);
		editTitle = (EditText) findViewById(R.id.editTitle);
		editTitle.setTypeface(typefaceBtn);
		editTitle.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				editTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
				textTitleHint.setVisibility(View.INVISIBLE);
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

		arrayTags = entryPojo.getArrayTags();

		textTitleHint = (TextView) findViewById(R.id.textTitleHint);
		textTitleHint.setVisibility(View.INVISIBLE);
		editTag = (EditText) findViewById(R.id.editTag);
		editTag.setTypeface(typefaceBtn);
		tagListAdapter = new TagListAdapter();
		listTags.setAdapter(tagListAdapter);

		btnAddTag = (ImageView) findViewById(R.id.btnAddTag);
		btnAddTag.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (editTag.getText().toString().trim().length() != 0) {
					arrayTags.add(0, editTag.getText().toString().trim());
					tagListAdapter.notifyDataSetChanged();
					editTag.setText("");
				}
			}
		});

		btnBack = (Button) findViewById(R.id.btnBack);
		btnBack.setTypeface(typefaceBtn);
		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setResult(Activity.RESULT_CANCELED);
				onBackPressed();
			}
		});

		btnFinish = (Button) findViewById(R.id.btnFinish);
		btnFinish.setTypeface(typefaceBtn);
		btnFinish.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (editTitle.getText().toString().trim().length() == 0) {

					editTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.signup_cross, 0);
					textTitleHint.setText(getString(R.string.enter_title));
					textTitleHint.setVisibility(View.VISIBLE);

				} else {

					Utility.ShowProgressDialog(mContext, getString(R.string.uploading));

					if (Utility.isNetworkAvailable(mContext)) {

					} else {

						Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
						Utility.HideDialog(mContext);
					}
				}
			}
		});
		
		editTitle.setText(entryPojo.getDescription());

	}

	public class TagListAdapter extends BaseAdapter {

		private LayoutInflater inflater = null;

		public TagListAdapter() {
			inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		}

		public int getCount() {
			return arrayTags.size();

		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View convertView, ViewGroup parent) {
			View vi = convertView;
			if (convertView == null)
				vi = inflater.inflate(R.layout.row_tag, null);

			TextView textTag = (TextView) vi.findViewById(R.id.textTag);
			textTag.setText(arrayTags.get(position));

			ImageView btnRemoveTag = (ImageView) vi.findViewById(R.id.btnRemoveTag);
			btnRemoveTag.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					arrayTags.remove(position);
					notifyDataSetChanged();
				}
			});

			return vi;
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}
}
