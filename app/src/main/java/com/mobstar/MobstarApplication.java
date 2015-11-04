package com.mobstar;

import android.app.Application;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.mobstar.utils.Constant;
import com.mobstar.utils.TimeUtility;

import java.util.HashMap;

import io.fabric.sdk.android.Fabric;


public class MobstarApplication extends Application{

	private Thread.UncaughtExceptionHandler androidDefaultUEH;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
        super.onCreate();
        if (Constant.IS_RELEASE) {
            Fabric.with(this, new Crashlytics());
            Constant.SERVER_URL="http://api.mobstar.com/";
        } else {
            Constant.SERVER_URL="http://api-mobstar.test.thinkmobiles.com/";
            Fabric.with(this, new Crashlytics());
        }
        new TimeUtility().requestServerTime();
        AdWordsManager.registerManager(getApplicationContext());
        //upload time--please uncomment instabug and uncaught exception handler
//		Instabug.initialize(this).setAnnotationActivityClass(InstabugAnnotationActivity.class).setShowIntroDialog(false).setEnableOverflowMenuItem(false);
//
//		androidDefaultUEH = Thread.getDefaultUncaughtExceptionHandler();
//		Thread.setDefaultUncaughtExceptionHandler(handler);
	}

	public enum TrackerName {
		APP_TRACKER, GLOBAL_TRACKER
	}

	HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

	public synchronized Tracker getTracker(TrackerName trackerId) {
		if (!mTrackers.containsKey(trackerId)) {
			GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
			analytics.setLocalDispatchPeriod(0);
			//analytics.getLogger().setLogLevel(LogLevel.VERBOSE);
			Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics.newTracker(getString(R.string.ga_id)) : (trackerId == TrackerName.GLOBAL_TRACKER) ? analytics
					.newTracker(R.xml.global_tracker) : null;
					mTrackers.put(trackerId, t);
		}
		return mTrackers.get(trackerId);
	}


	private Thread.UncaughtExceptionHandler handler = new Thread.UncaughtExceptionHandler() {
		public void uncaughtException(Thread thread, Throwable ex) {
			System.exit(1);
			Log.e("Mobstar Application", "Uncaught exception is: ", ex);
			androidDefaultUEH.uncaughtException(thread, ex);

		}
	};

}
