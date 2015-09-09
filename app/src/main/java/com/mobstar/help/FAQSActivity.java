package com.mobstar.help;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobstar.R;
import com.mobstar.pojo.FaqPojo;
import com.mobstar.utils.Constant;
import com.mobstar.utils.JSONParser;
import com.mobstar.utils.Utility;

public class FAQSActivity extends Activity {

	Context mContext;

	TextView textHelp;
	ImageView imgCategory;

	FAQAdapter faqAdapter;
	ListView listEntry;

	int current_visible_position = -1;

	ArrayList<FaqPojo> arrFaqPojos = new ArrayList<FaqPojo>();
	SharedPreferences preferences;
	public String sErrorMessage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_faqs);

		mContext = FAQSActivity.this;
		preferences = getSharedPreferences("mobstar_pref", Activity.MODE_PRIVATE);

		initControls();
		
		Utility.SendDataToGA("FAQ Screen", FAQSActivity.this);

	}

	private void initControls() {
		listEntry = (ListView) findViewById(R.id.listEntries);
		textHelp = (TextView) findViewById(R.id.textHelp);

		faqAdapter = new FAQAdapter();
		listEntry.setAdapter(faqAdapter);

		textHelp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});

		Utility.ShowProgressDialog(mContext, getString(R.string.loading));

		if (Utility.isNetworkAvailable(mContext)) {

			new FaqCall().start();

		} else {

			Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
			Utility.HideDialog(mContext);
		}
	}

	class FaqCall extends Thread {

		@Override
		public void run() {
			// TODO Auto-generated method stub

			String response = JSONParser.getRequest(Constant.SERVER_URL + Constant.FAQ, preferences.getString("token", null));

			//Log.v(Constant.TAG, "FaqCall response " + response);

			if (response != null) {

				try {

					JSONObject jsonObject = new JSONObject(response);

					if (jsonObject.has("error")) {
						sErrorMessage = jsonObject.getString("error");
					}

					if (jsonObject.has("faqs")) {

						JSONArray jsonArrayFaqs = jsonObject.getJSONArray("faqs");

						for (int i = 0; i < jsonArrayFaqs.length(); i++) {

							JSONObject jsonObjFaq = jsonArrayFaqs.getJSONObject(i);

							FaqPojo tempPojo = new FaqPojo();
							tempPojo.setFaqId(jsonObjFaq.getString("faqId"));
							tempPojo.setFaqQuestion(jsonObjFaq.getString("faqQuestion"));
							tempPojo.setFaqAnswer(jsonObjFaq.getString("faqAnswer"));

							arrFaqPojos.add(tempPojo);

						}
					}

					if (sErrorMessage != null && !sErrorMessage.equals("")) {
						handlerFaq.sendEmptyMessage(0);
					} else {
						handlerFaq.sendEmptyMessage(1);
					}

				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					handlerFaq.sendEmptyMessage(0);
				}

			} else {

				handlerFaq.sendEmptyMessage(0);
			}

		}
	}

	Handler handlerFaq = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Utility.HideDialog(mContext);

			if (msg.what == 1) {
				faqAdapter.notifyDataSetChanged();
			} else {

			}
		}
	};

	public class FAQAdapter extends BaseAdapter {

		public FAQAdapter() {

		}

		/* private view holder class */
		private class ViewHolder {

			LinearLayout llHeader, llDetail;
			ImageView imgHeaderImage;
			TextView textFaqHeader;
			TextView textFaqDetail;
		}

		@Override
		public int getCount() {
			return arrFaqPojos.size();
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;

			LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			if (convertView == null) {

				convertView = mInflater.inflate(R.layout.row_faqs, null);
				holder = new ViewHolder();

				holder.llHeader = (LinearLayout) convertView.findViewById(R.id.llHeader);
				holder.llDetail = (LinearLayout) convertView.findViewById(R.id.llDetail);

				holder.imgHeaderImage = (ImageView) convertView.findViewById(R.id.imgHeaderImage);
				holder.textFaqHeader = (TextView) convertView.findViewById(R.id.textFaqHeader);
				holder.textFaqDetail = (TextView) convertView.findViewById(R.id.textFaqDetail);

				convertView.setTag(holder);
			} else
				holder = (ViewHolder) convertView.getTag();

			holder.textFaqHeader.setText(arrFaqPojos.get(position).getFaqQuestion());

			holder.textFaqDetail.setText(arrFaqPojos.get(position).getFaqAnswer());

			holder.llHeader.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					if (current_visible_position != position) {
						current_visible_position = position;
					} else {
						current_visible_position = -1;
					}

					notifyDataSetChanged();
				}
			});

			if (current_visible_position == position) {
				holder.llDetail.setVisibility(View.VISIBLE);
				holder.llHeader.setBackgroundResource(R.color.yellow_color);
				holder.textFaqHeader.setTextColor(Color.parseColor(getString(R.color.white_color)));
				holder.imgHeaderImage.setImageResource(R.drawable.faq_question_white);

			} else {
				holder.llDetail.setVisibility(View.GONE);
				holder.llHeader.setBackgroundResource(R.color.white_color);
				holder.textFaqHeader.setTextColor(Color.parseColor(getString(R.color.black_color)));
				holder.imgHeaderImage.setImageResource(R.drawable.faq_question_gray);
			}

			return convertView;
		}
	}
}