package com.mobstar.utils;

import com.mobstar.AdWordsManager;

public class EntryActionHelper {

	public ActionListener actionListener;

	public interface ActionListener {
		public void onComplete();
	}

	public void LikeDislikeEntry(String[] name, String[] value, String token) {

		new LikeDislikeEntryAction(name, value, token).start();
	}

	public class LikeDislikeEntryAction extends Thread {

		String[] name, value;
		String token;

		public LikeDislikeEntryAction(String[] name, String[] value, String token) {

			this.name = name;
			this.value = value;
			this.token = token;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub

			String jsonString = JSONParser.postRequest(Constant.SERVER_URL + Constant.VOTE, name, value, token);

//			Log.d(Constant.TAG, "LikeEntry Response == " + jsonString);

			actionListener.onComplete();
            AdWordsManager.getInstance().sendEngagementEvent();
		}
	}
	public void setActionListener(ActionListener actionListener) {

		this.actionListener = actionListener;
	}
	

}
