package com.mobstar.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.hardware.Camera;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Display;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mobstar.MobstarApplication;
import com.mobstar.MobstarApplication.TrackerName;
import com.mobstar.R;
import com.mobstar.SplashActivity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utility {

	public static final String PROPERTY_REG_ID = "registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";

	static ProgressDialog dialog;
	private static boolean isSpinning=false;

	public static final String getCurrentDirectory(final Context context) {
		return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/Android/data/" + context.getPackageName() + "/";
	}

	public static void ShareLink(Context mContext, String link) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, link);
		intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Mobstar!");
		mContext.startActivity(Intent.createChooser(intent, "Share"));
	}

	public static String GetFileNameFromURl(String url) {

		if (url.indexOf('?') == -1) {
			return url.substring(url.lastIndexOf('/') + 1);
		} else {
			return url.substring(url.lastIndexOf('/') + 1, url.indexOf('?'));
		}

	}

	public static void SendDataToGA(String screenName, Activity activity) {

		Tracker t = ((MobstarApplication) activity.getApplication()).getTracker(TrackerName.APP_TRACKER);
		// Set screen name.
		// Where path is a String representing the screen name.
		t.setScreenName(screenName);

		// Send a screen view.
		t.send(new HitBuilders.AppViewBuilder().setNewSession().build());

		GoogleAnalytics analytics = GoogleAnalytics.getInstance(activity);
		analytics.dispatchLocalHits();

	}

	public static boolean IsValidEmail(EditText editText) {

		Pattern pattern = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}$");
		Matcher matcher = pattern.matcher(editText.getText().toString());

		if (matcher.matches()) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null;
	}

	public static void ShowProgressDialog(Context mContext, String message) {

		dialog = new ProgressDialog(mContext);
		dialog.setMessage(message);
		dialog.show();
	}

	public static void HideDialog(Context mContext) {
		if (mContext == null || dialog == null)
			return;
		if (!((Activity) mContext).isFinishing() && dialog.isShowing()) {
			dialog.dismiss();
		}
	}

	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	public static final int MEDIA_TYPE_AUDIO = 3;

	/** Create a File for saving an image or video */
	public static File getOutputMediaFile(int type,Context mContext) {
		String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()
				+ "/Android/data/" + mContext.getPackageName() +"/";
		//		File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), ".mobstar");
		File mediaStorageDir = new File(path);
		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				//				Log.d(Constant.TAG, "failed to create directory");
				return null;
			}
		}
		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
		} else if (type == MEDIA_TYPE_VIDEO) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_" + timeStamp + ".mp4");
		} else if (type == MEDIA_TYPE_AUDIO) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator + "AUD_" + timeStamp + ".mp3");
		} else {
			return null;
		}

		return mediaFile;
	}

	public static File getTemporaryMediaFile(Context mContext, String name) {
		String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()
				+ "/Android/data/" + mContext.getPackageName() +"/";
		//		File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), ".mobstar");
		File mediaStorageDir = new File(path);
		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Toast.makeText(mContext, "failed to create directory", Toast.LENGTH_SHORT).show();
				return null;
			}
		}
		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		File mediaFile;
			mediaFile = new File(mediaStorageDir.getPath() + File.separator + name + timeStamp + ".mp4");

		return mediaFile;
	}


	public static Bitmap rotate(Bitmap bitmap, int degree) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();

		Matrix mtx = new Matrix();
		mtx.postRotate(degree);

		return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
	}

	@SuppressLint("NewApi")
	public static String getPath(final Context context, final Uri uri) {

		String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()
				+ "/Android/data/" + context.getPackageName() +"/";

		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

		// DocumentProvider
		if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
			// ExternalStorageProvider
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				if ("primary".equalsIgnoreCase(type)) {
					//					return Environment.getExternalStorageDirectory() + "/" + split[1];
					return path + "/" + split[1];
				}

				// TODO handle non-primary volumes
			}
			// DownloadsProvider
			else if (isDownloadsDocument(uri)) {

				final String id = DocumentsContract.getDocumentId(uri);
				final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

				return getDataColumn(context, contentUri, null, null);
			}
			// MediaProvider
			else if (isMediaDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}

				final String selection = "_id=?";
				final String[] selectionArgs = new String[] { split[1] };

				return getDataColumn(context, contentUri, selection, selectionArgs);
			}
		}
		// MediaStore (and general)
		else if ("content".equalsIgnoreCase(uri.getScheme())) {
			return getDataColumn(context, uri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return null;
	}

	public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = { column };

		try {
			cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				final int column_index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(column_index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri.getAuthority());
	}

	public static int dpToPx(Context mContext, int dp) {

		DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
		int px = Math.round(dp * ((float) displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT));
		return px;

	}

	public static void DisLikeDialog(Activity activity) {
		final Dialog dialog = new Dialog(activity, R.style.DialogAnimationTheme);
		dialog.setContentView(R.layout.dialog_dislike);
		dialog.show();

		Timer timer = new Timer();
		TimerTask task = new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		};
		timer.schedule(task, 1000);
	}

	public static void LikeDialog(Activity activity) {
		try {
			final Dialog dialog = new Dialog(activity, R.style.DialogAnimationTheme);
			dialog.setContentView(R.layout.dialog_like);
			dialog.show();

			Timer timer = new Timer();
			TimerTask task = new TimerTask() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					dialog.dismiss();
				}
			};
			timer.schedule(task, 1000);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void MobitLikeDialog(Activity activity) {
		final Dialog dialog = new Dialog(activity, R.style.DialogAnimationTheme);
		dialog.setContentView(R.layout.dialog_mobit_like);
		dialog.show();

		Timer timer = new Timer();
		TimerTask task = new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		};
		timer.schedule(task, 1000);
	}

	// GCM Push Notification

	public static void storeRegistrationId(Context context, String regId) {
		final SharedPreferences prefs = getGCMPreferences(context);
		int appVersion = getAppVersion(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(PROPERTY_REG_ID, regId);
		editor.putInt(PROPERTY_APP_VERSION, appVersion);
		editor.commit();
	}

	public static SharedPreferences getGCMPreferences(Context context) {
		return context.getSharedPreferences(SplashActivity.class.getSimpleName(), Context.MODE_PRIVATE);
	}

	public static String getRegistrationId(Context context) {
		final SharedPreferences prefs = getGCMPreferences(context);
		String registrationId = prefs.getString(PROPERTY_REG_ID, "");
		if (registrationId.isEmpty()) {
			return "";
		}
		int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion) {
			return "";
		}
		return registrationId;
	}

	private static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	public final static String unescape_perl_string(String oldstr) {

		/*
		 * In contrast to fixing Java's broken regex charclasses, this one need
		 * be no bigger, as unescaping shrinks the string here, where in the
		 * other one, it grows it.
		 */

		StringBuffer newstr = new StringBuffer(oldstr.length());

		boolean saw_backslash = false;

		for (int i = 0; i < oldstr.length(); i++) {
			int cp = oldstr.codePointAt(i);
			if (oldstr.codePointAt(i) > Character.MAX_VALUE) {
				i++;
				/**** WE HATES UTF-16! WE HATES IT FOREVERSES!!! ****/
			}

			if (!saw_backslash) {
				if (cp == '\\') {
					saw_backslash = true;
				} else {
					newstr.append(Character.toChars(cp));
				}
				continue; /* switch */
			}

			if (cp == '\\') {
				saw_backslash = false;
				newstr.append('\\');
				newstr.append('\\');
				continue; /* switch */
			}

			switch (cp) {

			case 'r':
				newstr.append('\r');
				break; /* switch */

			case 'n':
				newstr.append('\n');
				break; /* switch */

			case 'f':
				newstr.append('\f');
				break; /* switch */

				/* PASS a \b THROUGH!! */
			case 'b':
				newstr.append("\\b");
				break; /* switch */

			case 't':
				newstr.append('\t');
				break; /* switch */

			case 'a':
				newstr.append('\007');
				break; /* switch */

			case 'e':
				newstr.append('\033');
				break; /* switch */

				/*
				 * A "control" character is what you get when you xor its codepoint
				 * with '@'==64. This only makes sense for ASCII, and may not yield
				 * a "control" character after all.
				 * 
				 * Strange but true: "\c{" is ";", "\c}" is "=", etc.
				 */
			case 'c': {
				if (++i == oldstr.length()) {
					die("trailing \\c");
				}
				cp = oldstr.codePointAt(i);
				/*
				 * don't need to grok surrogates, as next line blows them up
				 */
				if (cp > 0x7f) {
					die("expected ASCII after \\c");
				}
				newstr.append(Character.toChars(cp ^ 64));
				break; /* switch */
			}

			case '8':
			case '9':
				die("illegal octal digit");
				/* NOTREACHED */

				/*
				 * may be 0 to 2 octal digits following this one so back up one
				 * for fallthrough to next case; unread this digit and fall
				 * through to next case.
				 */
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
				--i;
				/* FALLTHROUGH */

				/*
				 * Can have 0, 1, or 2 octal digits following a 0 this permits
				 * larger values than octal 377, up to octal 777.
				 */
			case '0': {
				if (i + 1 == oldstr.length()) {
					/* found \0 at end of string */
					newstr.append(Character.toChars(0));
					break; /* switch */
				}
				i++;
				int digits = 0;
				int j;
				for (j = 0; j <= 2; j++) {
					if (i + j == oldstr.length()) {
						break; /* for */
					}
					/* safe because will unread surrogate */
					int ch = oldstr.charAt(i + j);
					if (ch < '0' || ch > '7') {
						break; /* for */
					}
					digits++;
				}
				if (digits == 0) {
					--i;
					newstr.append('\0');
					break; /* switch */
				}
				int value = 0;
				try {
					value = Integer.parseInt(oldstr.substring(i, i + digits), 8);
				} catch (NumberFormatException nfe) {
					die("invalid octal value for \\0 escape");
				}
				newstr.append(Character.toChars(value));
				i += digits - 1;
				break; /* switch */
			} /* end case '0' */

			case 'x': {
				if (i + 2 > oldstr.length()) {
					die("string too short for \\x escape");
				}
				i++;
				boolean saw_brace = false;
				if (oldstr.charAt(i) == '{') {
					/* ^^^^^^ ok to ignore surrogates here */
					i++;
					saw_brace = true;
				}
				int j;
				for (j = 0; j < 8; j++) {

					if (!saw_brace && j == 2) {
						break; /* for */
					}

					/*
					 * ASCII test also catches surrogates
					 */
					int ch = oldstr.charAt(i + j);
					if (ch > 127) {
						die("illegal non-ASCII hex digit in \\x escape");
					}

					if (saw_brace && ch == '}') {
						break; /* for */
					}

					if (!((ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'f') || (ch >= 'A' && ch <= 'F'))) {
						die(String.format("illegal hex digit #%d '%c' in \\x", ch, ch));
					}

				}
				if (j == 0) {
					die("empty braces in \\x{} escape");
				}
				int value = 0;
				try {
					value = Integer.parseInt(oldstr.substring(i, i + j), 16);
				} catch (NumberFormatException nfe) {
					die("invalid hex value for \\x escape");
				}
				newstr.append(Character.toChars(value));
				if (saw_brace) {
					j++;
				}
				i += j - 1;
				break; /* switch */
			}

			case 'u': {
				if (i + 4 > oldstr.length()) {
					die("string too short for \\u escape");
				}
				i++;
				int j;
				for (j = 0; j < 4; j++) {
					/* this also handles the surrogate issue */
					if (oldstr.charAt(i + j) > 127) {
						die("illegal non-ASCII hex digit in \\u escape");
					}
				}
				int value = 0;
				try {
					value = Integer.parseInt(oldstr.substring(i, i + j), 16);
				} catch (NumberFormatException nfe) {
					die("invalid hex value for \\u escape");
				}
				newstr.append(Character.toChars(value));
				i += j - 1;
				break; /* switch */
			}

			case 'U': {
				if (i + 8 > oldstr.length()) {
					die("string too short for \\U escape");
				}
				i++;
				int j;
				for (j = 0; j < 8; j++) {
					/* this also handles the surrogate issue */
					if (oldstr.charAt(i + j) > 127) {
						die("illegal non-ASCII hex digit in \\U escape");
					}
				}
				int value = 0;
				try {
					value = Integer.parseInt(oldstr.substring(i, i + j), 16);
				} catch (NumberFormatException nfe) {
					die("invalid hex value for \\U escape");
				}
				newstr.append(Character.toChars(value));
				i += j - 1;
				break; /* switch */
			}

			default:
				newstr.append('\\');
				newstr.append(Character.toChars(cp));
				/*
				 * say(String.format(
				 * "DEFAULT unrecognized escape %c passed through", cp));
				 */
				break; /* switch */

			}
			saw_backslash = false;
		}

		/* weird to leave one at the end */
		if (saw_backslash) {
			newstr.append('\\');
		}

		//Log.v("log_tag", "New String " + newstr.toString());

		return newstr.toString();
	}


	public final static String uniplus(String s) {
		if (s.length() == 0) {
			return "";
		}
		/* This is just the minimum; sb will grow as needed. */
		StringBuffer sb = new StringBuffer(2 + 3 * s.length());
		sb.append("U+");
		for (int i = 0; i < s.length(); i++) {
			sb.append(String.format("%X", s.codePointAt(i)));
			if (s.codePointAt(i) > Character.MAX_VALUE) {
				i++;
				/**** WE HATES UTF-16! WE HATES IT FOREVERSES!!! ****/
			}
			if (i + 1 < s.length()) {
				sb.append(".");
			}
		}
		return sb.toString();
	}

	private static final void die(String foa) {
		throw new IllegalArgumentException(foa);
	}

	private static final void say(String what) {
		System.out.println(what);
	}


	public static String GetStringTime(long seconds){

		String TimeInString = "";

		if (seconds <= 0) {
			TimeInString = "just now";
		} else if (seconds < 60) {
			TimeInString = seconds + " s ago";
		} else if (seconds < 3600) {
			TimeInString = seconds / 60 + " m ago";
		} else if (seconds < 86400) {
			TimeInString = seconds / 3600 + " h ago";
		} else if (seconds < 604800) {
			TimeInString = seconds / 86400 + " day ago";
		} else if (seconds < 2592000) {
			TimeInString = seconds / 604800 + " week ago";
		} else if (seconds < 31104000) {
			TimeInString = seconds / 2592000 + " month ago";
		} else if (seconds >= 31104000) {
			TimeInString = seconds / 31104000 + " year ago";
		}

		return TimeInString;
	}

	public static Bitmap getBitmapFromURL(String src) {
		try {
			URL url = new URL(src);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			Bitmap myBitmap = BitmapFactory.decodeStream(input);
			return myBitmap;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void setBadgeSamsung(Context context, int count) {
		String launcherClassName = getLauncherClassName(context);
		if (launcherClassName == null) {
			return;
		}
		Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
		intent.putExtra("badge_count", count);
		intent.putExtra("badge_count_package_name", context.getPackageName());
		intent.putExtra("badge_count_class_name", launcherClassName);
		context.sendBroadcast(intent);
	}

	public static void setBadgeSony(Context context, int count) {
		String launcherClassName = getLauncherClassName(context);
		if (launcherClassName == null) {
			return;
		}

		Intent intent = new Intent();
		intent.setAction("com.sonyericsson.home.action.UPDATE_BADGE");
		intent.putExtra("com.sonyericsson.home.intent.extra.badge.ACTIVITY_NAME", launcherClassName);
		intent.putExtra("com.sonyericsson.home.intent.extra.badge.SHOW_MESSAGE", true);
		intent.putExtra("com.sonyericsson.home.intent.extra.badge.MESSAGE", String.valueOf(count));
		intent.putExtra("com.sonyericsson.home.intent.extra.badge.PACKAGE_NAME", context.getPackageName());

		context.sendBroadcast(intent);
	}

	public static void setBadge(Context context, int count) {
		setBadgeSamsung(context, count);
		setBadgeSony(context, count);
	}

	public static void clearBadge(Context context) {
		setBadgeSamsung(context, 0);
		clearBadgeSony(context);
	}

	public static void clearBadgeSony(Context context) {
		String launcherClassName = getLauncherClassName(context);
		if (launcherClassName == null) {
			return;
		}

		Intent intent = new Intent();
		intent.setAction("com.sonyericsson.home.action.UPDATE_BADGE");
		intent.putExtra("com.sonyericsson.home.intent.extra.badge.ACTIVITY_NAME", launcherClassName);
		intent.putExtra("com.sonyericsson.home.intent.extra.badge.SHOW_MESSAGE", false);
		intent.putExtra("com.sonyericsson.home.intent.extra.badge.MESSAGE", String.valueOf(0));
		intent.putExtra("com.sonyericsson.home.intent.extra.badge.PACKAGE_NAME", context.getPackageName());

		context.sendBroadcast(intent);
	}


	public static String getLauncherClassName(Context context) {

		PackageManager pm = context.getPackageManager();

		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);

		List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
		for (ResolveInfo resolveInfo : resolveInfos) {
			String pkgName = resolveInfo.activityInfo.applicationInfo.packageName;
			if (pkgName.equalsIgnoreCase(context.getPackageName())) {
				String className = resolveInfo.activityInfo.name;
				return className;
			}
		}
		return null;
	}


}
