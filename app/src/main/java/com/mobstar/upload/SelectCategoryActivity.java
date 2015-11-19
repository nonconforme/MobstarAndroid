package com.mobstar.upload;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobstar.R;
import com.mobstar.api.ConnectCallback;
import com.mobstar.api.RestClient;
import com.mobstar.api.responce.BaseResponse;
import com.mobstar.api.responce.CategoryResponse;
//import com.mobstar.custom.CustomTextviewBold;

import com.mobstar.custom.CustomTextviewBold;
import com.mobstar.pojo.CategoryPojo;
import com.mobstar.utils.Constant;
import com.mobstar.utils.JSONParser;
import com.mobstar.utils.Utility;
import com.squareup.picasso.Picasso;

public class SelectCategoryActivity extends Activity implements OnClickListener {

	private Context mContext;

	//	ImageView btnMusic;
	private ListView listSelectCategory;
//	private String sErrorMessage="";
	private ArrayList<CategoryPojo> arrCategoryPojos = new ArrayList<CategoryPojo>();
	private CategoryAdapter categoryAdapter;
	private SharedPreferences preferences;
	private ImageButton btnClose;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_select_category);

		mContext = SelectCategoryActivity.this;

		InitControls();
		categoryAdapter=new CategoryAdapter();
		preferences = getSharedPreferences("mobstar_pref", Activity.MODE_PRIVATE);

		listSelectCategory.setAdapter(categoryAdapter);
		getCategoryRequest();

//		if (Utility.isNetworkAvailable(mContext)) {
//			new CategoryCall().start();
//		} else {
//			Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
//			Utility.HideDialog(mContext);
//		}

		Utility.SendDataToGA("SelectCategory Screen", SelectCategoryActivity.this);
	}

	void InitControls() {
		btnClose = (ImageButton) findViewById(R.id.btnClose);
		btnClose.setOnClickListener(this);
		listSelectCategory = (ListView) findViewById(R.id.listSelectCategory);

		//		btnMusic.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()){
			case R.id.btnClose:
				onBackPressed();
				break;
		}
		// TODO Auto-generated method stub
		//		if (view.equals(btnMusic)) {
		//			Intent intent = new Intent(mContext, SelectMediaTypeActivity.class);
		//			startActivity(intent);
		//			finish();
		//			overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		//		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}

	public class CategoryAdapter extends BaseAdapter {

		private LayoutInflater inflater = null;

		public CategoryAdapter() {
			inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return arrCategoryPojos.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		class ViewHolder {
			CustomTextviewBold btnAll;
			TextView textCategoryName;
			ImageView imageIcon;
			LinearLayout llCategory;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			final ViewHolder viewHolder;
			final CategoryPojo categoryObj=arrCategoryPojos.get(position);
			if (convertView == null) {

				convertView = inflater.inflate(R.layout.row_dialog_category, null);

				viewHolder = new ViewHolder();

				viewHolder.btnAll=(CustomTextviewBold)convertView.findViewById(R.id.btnAll);
				viewHolder.textCategoryName = (TextView) convertView.findViewById(R.id.textCategoryName);
				viewHolder.imageIcon=(ImageView)convertView.findViewById(R.id.image_icon);
				viewHolder.llCategory=(LinearLayout)convertView.findViewById(R.id.llCategory);
				convertView.setTag(viewHolder);

			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}


			viewHolder.btnAll.setVisibility(View.GONE);


			if(categoryObj.getCategoryName()!=null && categoryObj.getCategoryName().length()>0){
				viewHolder.textCategoryName.setText(categoryObj.getCategoryName());
			}

			if(categoryObj.getCategoryDescription()!=null && categoryObj.getCategoryDescription().length()>0) {
				Picasso.with(mContext).load(categoryObj.getCategoryDescription()).placeholder(R.drawable.ic_pic_small).into(viewHolder.imageIcon);
			}
			else {
				Picasso.with(mContext).load(R.drawable.ic_pic_small).into(viewHolder.imageIcon);
			}


			//set background
			if(categoryObj.getCategoryActive()){
				if(categoryObj.getID().equalsIgnoreCase("8")){
					viewHolder.llCategory.setBackground(getResources().getDrawable(R.drawable.oval_red_button_background));
				}
				else {
					viewHolder.llCategory.setBackground(getResources().getDrawable(R.drawable.oval_yellow_button_background));
					viewHolder.textCategoryName.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
					viewHolder.textCategoryName.setCompoundDrawablePadding(0);
				}
			}
			else {
				viewHolder.llCategory.setBackground(getResources().getDrawable(R.drawable.coming_soon_bg));
//				viewHolder.textCategoryName.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.coming_soon,0);
				viewHolder.textCategoryName.setText(getString(R.string.coming_soon));

				viewHolder.textCategoryName.setCompoundDrawablePadding(5);
			}

			viewHolder.btnAll.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					//					Intent intent = new Intent(mContext, SelectMediaTypeActivity.class);
					//					intent.putExtra("categoryId",categoryObj.getID());
					//					startActivity(intent);
					//					finish();
					//					overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

					onBackPressed();
				}
			});

			viewHolder.llCategory.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					if(categoryObj.getCategoryActive()){
						if(categoryObj.getID().equalsIgnoreCase("3")){ // pass model category id
							startSelectSubCategoryActivity(categoryObj.getID());
						}
						else{
							startSelectMediaTypeActivity(categoryObj.getID());
						}

					}
				}
			});

			return convertView;

		}

	}

	private void startSelectSubCategoryActivity(final String categoryId){
		final Intent intent = new Intent(mContext, SelectSubCategoryActivity.class);
		intent.putExtra("categoryId", categoryId);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}

	private void startSelectMediaTypeActivity(final String categoryId){
		final Intent intent = new Intent(mContext, SelectMediaTypeActivity.class);
		intent.putExtra("categoryId", categoryId);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}

	private void getCategoryRequest(){
		RestClient.getInstance(this).getRequest(Constant.GET_CATEGORY, null, new ConnectCallback<CategoryResponse>() {

			@Override
			public void onSuccess(CategoryResponse object) {
				if (object.hasError()) {
					OkayAlertDialog(object.getErrorMessage());
				} else {
					arrCategoryPojos = object.getCategoryPojos();
					categoryAdapter.notifyDataSetChanged();
				}
			}

			@Override
			public void onFailure(String error) {
				OkayAlertDialog(error);
			}
		});
	}

	void OkayAlertDialog(final String msg) {
		if (msg == null)
			return;

		if (!isFinishing()) {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);

					// set title
					alertDialogBuilder.setTitle(getResources().getString(R.string.app_name));

					// set dialog message
					alertDialogBuilder.setMessage(msg).setCancelable(false).setNeutralButton("OK", null);

					// create alert dialog
					AlertDialog alertDialog = alertDialogBuilder.create();

					// show it
					alertDialog.show();
				}
			});
		}

	}


}
