package com.mobstar.home;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobstar.R;
import com.mobstar.pojo.MentorPojo;
import com.mobstar.utils.Constant;
import com.mobstar.utils.JSONParser;
import com.mobstar.utils.Utility;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class MentorsFragment extends Fragment {

	Context mContext;
	TextView textMentors;
	SharedPreferences preferences;

	ListView listMentor;
	MentorListAdapter mentorListAdapter;

	public String sErrorMessage;

	ArrayList<MentorPojo> arrMentorPojos;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		View view = inflater.inflate(R.layout.fragment_mentors, container, false);

		mContext = getActivity();
		preferences = getActivity().getSharedPreferences("mobstar_pref", Activity.MODE_PRIVATE);

		arrMentorPojos = new ArrayList<MentorPojo>();

		Utility.SendDataToGA("Mentor Screen", getActivity());

		return view;

	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);

		textMentors = (TextView) view.findViewById(R.id.textMentors);
		textMentors.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				getActivity().onBackPressed();
			}
		});

		listMentor = (ListView) view.findViewById(R.id.listMentor);
		mentorListAdapter = new MentorListAdapter();
		listMentor.setAdapter(mentorListAdapter);

		Utility.ShowProgressDialog(mContext, getString(R.string.loading));

		if (Utility.isNetworkAvailable(mContext)) {

			new MentorCall().start();

		} else {

			Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
			Utility.HideDialog(mContext);
		}

	}

	class MentorCall extends Thread {

		@Override
		public void run() {
			// TODO Auto-generated method stub

			String response = JSONParser.getRequest(Constant.SERVER_URL + Constant.GET_MENTOR, preferences.getString("token", null));

			// Log.v(Constant.TAG, "MentorCall response " + response);

			if (response != null) {

				try {

					JSONObject jsonObject = new JSONObject(response);

					if (jsonObject.has("error")) {
						sErrorMessage = jsonObject.getString("error");
					}

					if (jsonObject.has("mentors")) {

						JSONArray jsonArrayMentor = jsonObject.getJSONArray("mentors");

						for (int i = 0; i < jsonArrayMentor.length(); i++) {

							JSONObject jsonObjMentorRow = jsonArrayMentor.getJSONObject(i);

							JSONObject jsonObjMentor = jsonObjMentorRow.getJSONObject("mentor");

							MentorPojo mentorPojo = new MentorPojo();
							mentorPojo.setMentorID(jsonObjMentor.getString("id"));
							mentorPojo.setDisplayName(jsonObjMentor.getString("displayName"));
							mentorPojo.setProfilePicture(jsonObjMentor.getString("profilePicture"));
							mentorPojo.setInfo(jsonObjMentor.getString("info"));
							if (jsonObjMentor.has("category")) {
								mentorPojo.setCategory(jsonObjMentor.getString("category"));
							}
							arrMentorPojos.add(mentorPojo);

						}
					}

					if (sErrorMessage != null && !sErrorMessage.equals("")) {
						handlerMentor.sendEmptyMessage(0);
					} else {
						handlerMentor.sendEmptyMessage(1);
					}

				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					handlerMentor.sendEmptyMessage(0);
				}

			} else {

				handlerMentor.sendEmptyMessage(0);
			}

		}
	}

	Handler handlerMentor = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Utility.HideDialog(mContext);

			if (msg.what == 1) {
				mentorListAdapter.notifyDataSetChanged();
			} else {

			}
		}
	};

	public class MentorListAdapter extends BaseAdapter {

		private LayoutInflater inflater = null;

		public MentorListAdapter() {
			inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		}

		public int getCount() {
			return arrMentorPojos.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View convertView, ViewGroup parent) {

			final ViewHolder viewHolder;

			if (convertView == null) {

				convertView = inflater.inflate(R.layout.row_mentor, null);

				viewHolder = new ViewHolder();

				viewHolder.textMentorName = (TextView) convertView.findViewById(R.id.textMentorName);
				viewHolder.textCategory = (TextView) convertView.findViewById(R.id.textCategory);
				viewHolder.textMentorInfo = (TextView) convertView.findViewById(R.id.textMentorInfo);
				viewHolder.imgMentor = (ImageView) convertView.findViewById(R.id.imgMentor);
				viewHolder.imgPlaceHolder = (ImageView) convertView.findViewById(R.id.imgPlaceHolder);
				convertView.setTag(viewHolder);

			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			viewHolder.textMentorName.setText(arrMentorPojos.get(position).getDisplayName());
			viewHolder.textCategory.setText(arrMentorPojos.get(position).getCategory());
			viewHolder.textMentorInfo.setText(arrMentorPojos.get(position).getInfo());

			viewHolder.imgPlaceHolder.setImageResource(R.drawable.image_placeholder);
			viewHolder.imgPlaceHolder.setVisibility(View.VISIBLE);

			viewHolder.imgMentor.setVisibility(View.GONE);

			if (arrMentorPojos.get(position).getProfilePicture().equals("")) {

			} else {

				Picasso.with(mContext).load(arrMentorPojos.get(position).getProfilePicture()).resize(Utility.dpToPx(mContext, 332), Utility.dpToPx(mContext, 360)).centerCrop()
						.placeholder(R.drawable.ic_pic_small).error(R.drawable.ic_pic_small).into(viewHolder.imgMentor, new Callback() {

							@Override
							public void onSuccess() {
								// TODO Auto-generated method stub
								viewHolder.imgMentor.setVisibility(View.VISIBLE);
							}

							@Override
							public void onError() {
								// TODO Auto-generated method stub

							}

						});

//				Ion.with(mContext).load(arrMentorPojos.get(position).getProfilePicture()).withBitmap().placeholder(R.drawable.image_placeholder)
//						.error(R.drawable.image_placeholder).resize(Utility.dpToPx(mContext, 332), Utility.dpToPx(mContext, 360)).centerCrop().asBitmap()
//						.setCallback(new FutureCallback<Bitmap>() {
//
//							@Override
//							public void onCompleted(Exception exception, Bitmap bitmap) {
//								// TODO Auto-generated method stub
//								if (exception == null) {
//									viewHolder.imgMentor.setImageBitmap(bitmap);
//									viewHolder.imgMentor.setVisibility(View.VISIBLE);
//								} else {
//									// Log.v(Constant.TAG, "Exception " +
//									// exception.toString());
//								}
//							}
//						});
			}

			return convertView;
		}

		class ViewHolder {

			TextView textMentorName, textCategory, textMentorInfo;
			ImageView imgMentor;
			ImageView imgPlaceHolder;

		}
	}
}
