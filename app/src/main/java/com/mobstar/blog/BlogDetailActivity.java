package com.mobstar.blog;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobstar.R;
import com.mobstar.pojo.BlogPojo;
import com.mobstar.utils.Utility;
import com.squareup.picasso.Picasso;

public class BlogDetailActivity extends Activity{
	
	private Context mContext;
	private ImageView imgBlog;
	private TextView textBlog,textDate,textTitle,textDescription;
	private BlogPojo blogPojo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_blog_detail);
		mContext=BlogDetailActivity.this;
		
		Utility.SendDataToGA("BlogDetail Screen",BlogDetailActivity.this);
		
		Bundle bundle=getIntent().getExtras();
		if(bundle!=null){
			blogPojo = (BlogPojo) getIntent().getSerializableExtra("blog");
		}
		initControlls();
	}

	private void initControlls() {
		textBlog=(TextView) findViewById(R.id.textBlog);
		textBlog.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		imgBlog=(ImageView) findViewById(R.id.imgBlog);
		Picasso.with(mContext).load(blogPojo.getBlogImage()).into(imgBlog);
		
		textDate=(TextView) findViewById(R.id.textDate);
		textDate.setText(blogPojo.getCreatedAt());
		
		textTitle=(TextView) findViewById(R.id.textTitle);
		textTitle.setText(blogPojo.getBlogTitle());
		
		textDescription=(TextView) findViewById(R.id.textDescription);
		textDescription.setText(blogPojo.getDescription());
		
	}

}
