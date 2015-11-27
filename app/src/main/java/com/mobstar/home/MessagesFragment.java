//package com.mobstar.home;
//
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.ListView;
//import android.widget.TextView;
//
//import com.mobstar.R;
//import com.mobstar.utils.Utility;
//
//public class MessagesFragment extends Fragment {
//
//	TextView textMessages;
//
//	Context mContext;
//
//	SharedPreferences preferences;
//
//	ListView listMessage;
//	MessageListAdapter messageListAdapter;
//
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//		// TODO Auto-generated method stub
//		View view = inflater.inflate(R.layout.fragment_messages, container, false);
//		mContext = getActivity();
//		Utility.SendDataToGA("Messages Screen", getActivity());
//
//		return view;
//	}
//
//	@Override
//	public void onViewCreated(View view, Bundle savedInstanceState) {
//		// TODO Auto-generated method stub
//		super.onViewCreated(view, savedInstanceState);
//
//		textMessages = (TextView) view.findViewById(R.id.textMessages);
//		textMessages.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View view) {
//				getActivity().onBackPressed();
//			}
//		});
//
//		listMessage = (ListView) view.findViewById(R.id.listMessage);
//		messageListAdapter = new MessageListAdapter();
//		listMessage.setAdapter(messageListAdapter);
//
//	}
//
//	public class MessageListAdapter extends BaseAdapter {
//
//		private LayoutInflater inflater = null;
//
//		public MessageListAdapter() {
//			inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//		}
//
//		public int getCount() {
//			return 10;
//		}
//
//		public Object getItem(int position) {
//			return position;
//		}
//
//		public long getItemId(int position) {
//			return position;
//		}
//
//		public View getView(final int position, View convertView, ViewGroup parent) {
//
//			final ViewHolder viewHolder;
//
//			if (convertView == null) {
//
//				convertView = inflater.inflate(R.layout.row_comment, null);
//
//				viewHolder = new ViewHolder();
//				viewHolder.textUserName = (TextView) convertView.findViewById(R.id.textUserName);
//				viewHolder.textTime = (TextView) convertView.findViewById(R.id.textTime);
//				viewHolder.textDescription = (TextView) convertView.findViewById(R.id.textDescription);
//				convertView.setTag(viewHolder);
//
//			} else {
//				viewHolder = (ViewHolder) convertView.getTag();
//			}
//			return convertView;
//		}
//
//		class ViewHolder {
//			TextView textUserName, textDescription, textTime;
//		}
//	}
//}
