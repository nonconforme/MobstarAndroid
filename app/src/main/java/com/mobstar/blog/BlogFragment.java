package com.mobstar.blog;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobstar.R;
import com.mobstar.home.ShareActivity;
import com.mobstar.pojo.BlogPojo;
import com.mobstar.utils.Constant;
import com.mobstar.utils.JSONParser;
import com.mobstar.utils.Utility;
import com.squareup.picasso.Picasso;

public class BlogFragment extends Fragment {

	private Context mContext;

	private SharedPreferences preferences;

	private ListView listBlog;

	private String sErrorMessage;

	private ArrayList<BlogPojo> arrBlog=new ArrayList<BlogPojo>();

	private BlogAdapter blogAdapter;

	private TextView textNoData;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_blog, container, false);

		mContext = getActivity();

		preferences = mContext.getSharedPreferences("mobstar_pref", Context.MODE_PRIVATE);


		Utility.SendDataToGA("Blog Screen", getActivity());

		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);

		textNoData=(TextView) view.findViewById(R.id.textNoData);
		textNoData.setVisibility(View.GONE);

		TextView textBlogHeader = (TextView) view.findViewById(R.id.textBlogHeader);
		textBlogHeader.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getActivity().onBackPressed();
			}
		});

		listBlog = (ListView) view.findViewById(R.id.listBlog);
		blogAdapter=new BlogAdapter();
		listBlog.setAdapter(blogAdapter);
		Utility.ShowProgressDialog(mContext, getString(R.string.loading));

		if (Utility.isNetworkAvailable(mContext)) {
			new BlogCall().start();
		} else {
			Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
			Utility.HideDialog(mContext);
		}
		
		listBlog.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(mContext, BlogDetailActivity.class);
				intent.putExtra("blog", arrBlog.get(position));
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
				
			}
		});

	}

	public class BlogAdapter extends BaseAdapter {

		private class ViewHolder {
			TextView textDate;
			ImageView imgBlog;
			TextView textBlog;
		}

		@Override
		public int getCount() {
			return arrBlog.size();
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
			final ViewHolder viewHolder;

			LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			if (convertView == null) {

				convertView = mInflater.inflate(R.layout.row_item_blog, null);
				viewHolder = new ViewHolder();

				viewHolder.textDate = (TextView) convertView.findViewById(R.id.textDate);
				viewHolder.imgBlog = (ImageView) convertView.findViewById(R.id.imgBlog);
				viewHolder.textBlog = (TextView) convertView.findViewById(R.id.textBlog);

				convertView.setTag(viewHolder);
			} else
				viewHolder = (ViewHolder) convertView.getTag();

			viewHolder.textDate.setText(arrBlog.get(position).getCreatedAt());
			viewHolder.textBlog.setText(arrBlog.get(position).getDescription());

			if (arrBlog.get(position).getBlogImage().equals("")) {
				viewHolder.imgBlog.setImageResource(R.drawable.ic_pic_small);
			} else {
				viewHolder.imgBlog.setImageResource(R.drawable.ic_pic_small);

				Picasso.with(mContext).load(arrBlog.get(position).getBlogImage()).resize(Utility.dpToPx(mContext, 45), Utility.dpToPx(mContext, 45)).centerCrop()
				.placeholder(R.drawable.ic_pic_small).error(R.drawable.ic_pic_small).into(viewHolder.imgBlog);

			}

			return convertView;
		}
	}

	class BlogCall extends Thread {

		@Override
		public void run() {
			// TODO Auto-generated method stub

			String response = JSONParser.getRequest(Constant.SERVER_URL + Constant.BOLG, preferences.getString("token", null));

			// Log.v(Constant.TAG, "FanCall response " + response);

			if (response != null) {

				try {

					sErrorMessage = "";

					JSONObject jsonObject = new JSONObject(response);

					if (jsonObject.has("error")) {
						sErrorMessage = jsonObject.getString("error");
					}

					JSONArray jsonArrayStars = jsonObject.getJSONArray("blogs");

					for (int j = 0; j < jsonArrayStars.length(); j++) {

						JSONObject jsonObjBlog = jsonArrayStars.getJSONObject(j);

//						JSONObject jsonObjBlog = jsonObjStar.getJSONObject("blog");

						BlogPojo tempPojo = new BlogPojo();
						tempPojo.setID(jsonObjBlog.getString("id"));
						tempPojo.setBlogTitle(jsonObjBlog.getString("blogTitle"));
						tempPojo.setBlogHeader(jsonObjBlog.getString("blogHeader"));
						tempPojo.setBlogImage(jsonObjBlog.getString("blogImage"));
						tempPojo.setDescription(jsonObjBlog.getString("description"));
						tempPojo.setCreatedAt(jsonObjBlog.getString("CreatedAt"));
						arrBlog.add(tempPojo);
					}

					if (sErrorMessage != null && !sErrorMessage.equals("")) {
						handlerBlog.sendEmptyMessage(0);
					} else {
						handlerBlog.sendEmptyMessage(1);
					}

				} catch (Exception e) {
					e.printStackTrace();
					handlerBlog.sendEmptyMessage(0);
				}

			} else {

				handlerBlog.sendEmptyMessage(0);
			}

		}
	}

	Handler handlerBlog = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Utility.HideDialog(mContext);

			if (arrBlog.size() == 0) {
				textNoData.setVisibility(View.VISIBLE);
			} else {
				textNoData.setVisibility(View.GONE);
			}

			if (msg.what == 1) {
				blogAdapter.notifyDataSetChanged();
			} else {

			}
		}
	};


}
