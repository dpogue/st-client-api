package com.socialtext.www;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class dm_svc extends Service {


	public static boolean r = true;

	private static final String PREFS_NAME = "dm_settings";
	private String username;
	private String password;
	public static int msg_count=0;
	public static String server;
	private String tme = new java.text.SimpleDateFormat("yyyy-MM-dd%20HH:mm:ss").format(new Date());
	private int poll; //This contains a index number for the poll len below
	//poll time in ms
	private final int[] poll_len = {60000, 180000, 300000, 600000, 900000, 1200000,1800000,3600000};

	private static final int NOTIFYID = 1;
	private Notification notification;
	private PendingIntent contentIntent;

	@Override
	public void onCreate() {
		super.onCreate();
		
		setupNotification();

		Thread t = new Thread() {
			public void run() {

				while(r){
					try {
						getSettings();
						GetSignal();
						Thread.sleep(poll_len[poll]);
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}
			}
		};

		t.start();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		Toast.makeText(this, "SocialText Signals Stopped", Toast.LENGTH_LONG).show();

	}

	public void GetSignal() throws Throwable { //TODO make more flexible

		URI url = new URI(server + "/data/signals?after="+tme);
		HttpParams params = new BasicHttpParams();
		params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1); //Speeds up the Http POST
		DefaultHttpClient httpclient = new DefaultHttpClient(params);

		HttpResponse response;
		HttpGet httpget = new HttpGet(url);

		//Authenticates with the server HTTP Basic
		httpclient.getCredentialsProvider().setCredentials(new AuthScope(null, -1), new UsernamePasswordCredentials(username, password));


		try {

			response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();

			if (entity != null) {

				InputStream instream = entity.getContent();
				String result= convertStreamToString(instream);

				Log.v("Result", result);
				
				if((msg_count=countOccurrences(result,"<li>")) > 0){
					showNotification();
					tme = new java.text.SimpleDateFormat("yyyy-MM-dd%20HH:mm:ss").format(new Date());
				}
				
			}

		}catch (Throwable err) {
			Log.e("Signal Error" , err.toString());
		}
	}
	
	private void setupNotification(){
		
		notification = new Notification(R.drawable.notify_signal, "New Signal Received", System.currentTimeMillis());

		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.flags |= Notification.DEFAULT_VIBRATE;
		notification.flags |= Notification.DEFAULT_SOUND;
		
	}

	private void showNotification(){

		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
		CharSequence contentTitle = "SocialText Signals";
		CharSequence contentText = "";
		
		//Open the browser on message selection
		Intent open_signal = new Intent(dm_svc.this, dm_web.class);
		
		Intent notificationIntent = open_signal;
		contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

		if(msg_count>1){
			contentText = "New signals received ("+msg_count+")";
		}
		else{
			contentText = "New signal received";
		}

		notification.setLatestEventInfo(getApplicationContext(), contentTitle, contentText, contentIntent);
		mNotificationManager.notify(NOTIFYID, notification);
	}

	private void getSettings(){

		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

		server = settings.getString("url", "https://www.socialtext.com");
		username = settings.getString("username", "Username");
		password = settings.getString("password", "password");
		poll = settings.getInt("poll", 3);
	}


	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	private static String convertStreamToString(InputStream is) {
		/*
		 * To convert the InputStream to String we use the BufferedReader.readLine()
		 * method. We iterate until the BufferedReader return null which means
		 * there's no more data to read. Each line will appended to a StringBuilder
		 * and returned as String.
		 */
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	public static int countOccurrences(String arg1, String arg2) {
		int count = 0;
		int index = 0;
		while ((index = arg1.indexOf(arg2, index)) != -1) {
			++index;
			++count;
		}

		return count;

	}



}