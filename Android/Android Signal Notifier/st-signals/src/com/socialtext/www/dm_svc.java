package com.socialtext.www;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.entity.StringEntity;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;

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

// TODO: Auto-generated Javadoc
/**
 * The Class dm_svc.
 */
public class dm_svc extends Service {

	/**
	 * The Enum Mimetype.
	 */
	public enum Mimetype {
		
		/** The TEXT. */
		TEXT("text/plain"), 
 /** The HTML. */
 HTML("text/html"), 
 /** The JSON. */
 JSON("application/json"), 
 /** The WIKI. */
 WIKI(
				"application/x-socialtext-wiki");

		/** The m_mimetype. */
		private String m_mimetype;

		/**
		 * Instantiates a new mimetype.
		 *
		 * @param type the type
		 */
		private Mimetype(String type) {
			m_mimetype = type;
		}

		/**
		 * Gets the type.
		 *
		 * @return the type
		 */
		public String getType() {
			return m_mimetype;
		}
	}

	/**
	 * The Enum Method.
	 */
	public enum Method {
		
		/** The GET. */
		GET, 
 /** The PUT. */
 PUT, 
 /** The POST. */
 POST, 
 /** The DELETE. */
 DELETE;
	}

	/** The r. */
	public static boolean r = true;

	/** The Constant PREFS_NAME. */
	private static final String PREFS_NAME = "dm_settings";
	
	/** The username. */
	private String username;
	
	/** The password. */
	private String password;
	
	/** The cookies. */
	private CookieStore cookies;
	
	/** The msg_count. */
	public static int msg_count = 0;
	
	/** The server. */
	public static String server;
	
	/** The id. */
	private String id;
	
	/** The sequence. */
	private int sequence = 0;
	
	/** The init. */
	private boolean init = false;
	
	/** The signal_contents. */
	private String signal_contents;
	
	/** The signal_author. */
	private String signal_author;
	
	/** The m_default_mime. */
	private Mimetype m_default_mime = Mimetype.JSON; /* JSON by default */

	/** The Constant NOTIFYID. */
	private static final int NOTIFYID = 1;
	
	/** The notification. */
	private Notification notification;
	
	/** The content intent. */
	private PendingIntent contentIntent;

	/* (non-Javadoc)
	 * @see android.app.Service#onCreate()
	 */
	@Override
	public void onCreate() {
		super.onCreate();

		getSettings();
		setupNotification();

		getCookie();

		Thread t = new Thread() {
			public void run() {

				while (r) {
					try {
						
						if (init == false) {
							pushRead(pushConnect());
						}
						JSONArray response = pushPoll(id, (sequence == 0 ? "0"
								: Integer.toString(sequence)));
						
						pushRead(response);
						Log.v("Data", response.toString());
						
						getSettings(); // update settings if they've changed
						Thread.sleep(300000);

					} catch (Throwable e) {
						e.printStackTrace();
					}
				}
			}
		};

		t.start();
	}

	/**
	 * Push read.
	 *
	 * @param cmds the cmds
	 */
	private void pushRead(JSONArray cmds) {
		try {
		
			for (int i = 0; i < cmds.length(); i++) {
				PushObject po = new PushObject(cmds.getJSONObject(i).toString());

				if (po.getObjClass().equals("command")) {
					String comm = po.getObject().getString("command");
					if (comm.equals("welcome")) {
						id = po.getObject().getString("client_id");
						Log.v("id", id);
						init = true;
					} else if (comm.equals("goodbye")) {
						init = false;
					} else if (comm.equals("continue")) {
						sequence = po.getObject().getInt("sequence");
					} else {
						/* Unhandled Command! */
					}
				}
				else if (po.getObjClass().equals("signal")) {
					signal_author = po.getObject()
							.getString("best_full_name");
					signal_contents = po.getObject().getString(
							"body");
					msg_count++;
					showNotification();
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see android.app.Service#onDestroy()
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();

		Toast.makeText(this, "Socialtext Signals Stopped", Toast.LENGTH_LONG)
				.show();

	}

	/**
	 * Gets the cookie.
	 *
	 * @return the cookie
	 */
	private void getCookie() {

		HttpResponse response;
		HttpParams params = new BasicHttpParams();
		params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
				HttpVersion.HTTP_1_1); // Speeds up the Http POST
		DefaultHttpClient httpclient = new DefaultHttpClient(params);

		// don't follow redirects
		httpclient.getParams().setBooleanParameter(
				ClientPNames.HANDLE_REDIRECTS, false);

		try {
			HttpPost httppost = new HttpPost(server + "/nlw/submit/login");
			httppost.setHeader("Content-Type",
					"application/x-www-form-urlencoded");

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
			nameValuePairs.add(new BasicNameValuePair("username", username));
			nameValuePairs.add(new BasicNameValuePair("password", password));
			nameValuePairs.add(new BasicNameValuePair("remember", "1"));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,
					HTTP.UTF_8));

			response = httpclient.execute(httppost);

			if (response.getStatusLine().toString().contains("302 Found")) {
				cookies = httpclient.getCookieStore();
			}

		} catch (IOException e) {
			System.err.println(e.toString());
		}
	}

	/**
	 * Private function to actually send the HTTP request. Maybe we need a
	 * callback here that is passed as a parameter too?
	 *
	 * @param url The url to request.
	 * @param method The method (GET, POST, PUT, DELETE)
	 * @param contenttype The content type (text/html, text/plain,
	 * text/x.socialtext-wiki, application/json)
	 * @param accepttype The type we accept (see contenttype for values)
	 * @param data The data that we are sending (if any)
	 * @return the string
	 */
	private String request(String url, Method method, Mimetype contenttype,
			Mimetype accepttype, String data) {
		accepttype = (accepttype != null) ? accepttype : m_default_mime;
		contenttype = (contenttype != null) ? contenttype : m_default_mime;

		URI uri;
		try {
			uri = new URI(server + url);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return e.toString();
		}
		HttpParams params = new BasicHttpParams();
		params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
				HttpVersion.HTTP_1_1);
		DefaultHttpClient httpclient = new DefaultHttpClient(params);
		httpclient.setCookieStore(cookies); // set the cookie store
		HttpResponse response;
		HttpRequestBase httpreq;
		if (method == Method.POST) {
			httpreq = new HttpPost(uri);
			/* We need to set the request body here... */
			try {
				StringEntity postbody = new StringEntity(data, "UTF-8");
				((HttpPost) httpreq).setEntity(postbody);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return e.toString();
			}
		} else {
			/* Default to GET */
			httpreq = new HttpGet(uri);
		}
		httpreq.setHeader("Accept", accepttype.getType());
		httpreq.setHeader("Content-Type", contenttype.getType());

		try {
			response = httpclient.execute(httpreq);
			HttpEntity entity = response.getEntity();

			if (entity != null) {
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				entity.writeTo(os);
				String result = os.toString("UTF8");

				return result;
			}
			return null;
		} catch (Throwable e) {
			/* Error handling? What Error handling? :D */
			e.printStackTrace();
			return e.toString();
		}
	}

	/**
	 * Push connect.
	 *
	 * @return the jSON array
	 */
	public JSONArray pushConnect() {
		String path = "/data/push?nowait=1";
		String json = request(path, Method.GET, Mimetype.JSON, Mimetype.JSON,
				null);
		try {
			JSONArray sigs = new JSONArray(json);
			return sigs;

		} catch (JSONException e) {
			System.out.println(json);
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Push poll.
	 *
	 * @param id the id
	 * @param sequence the sequence
	 * @return the jSON array
	 */
	public JSONArray pushPoll(String id, String sequence) {
		String path = "/data/push?client_id=" + id + ";sequence=" + sequence;
		String json = request(path, Method.GET, Mimetype.JSON, Mimetype.JSON,
				null);
		try {
			JSONArray sigs = new JSONArray(json);
			return sigs;

		} catch (JSONException e) {
			System.out.println(json);
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Setup notification.
	 */
	private void setupNotification() {

		notification = new Notification(R.drawable.notify_signal,
				"New Signal Received", System.currentTimeMillis());

		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.defaults |= Notification.DEFAULT_ALL;
	}

	/**
	 * Show notification.
	 */
	private void showNotification() {

		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
		CharSequence contentTitle = "Socialtext Signals";
		CharSequence contentText = "";

		// Open the browser on message selection
		Intent open_signal = new Intent(dm_svc.this, dm_web.class);

		Intent notificationIntent = open_signal;
		contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
				0);

		if (msg_count > 1) {
			contentText = "New signals received (" + msg_count + ")";
		} else {
			contentText = signal_author + ":" + signal_contents;
		}

		notification.setLatestEventInfo(getApplicationContext(), contentTitle,
				contentText, contentIntent);
		mNotificationManager.notify(NOTIFYID, notification);
	}

	/**
	 * Gets the settings.
	 *
	 * @return the settings
	 */
	private void getSettings() {

		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

		server = settings.getString("url", "https://www.socialtext.com");
		username = settings.getString("username", "Username");
		password = settings.getString("password", "password");
	}

	/* (non-Javadoc)
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
}