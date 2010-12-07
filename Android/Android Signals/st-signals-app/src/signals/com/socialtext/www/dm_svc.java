package signals.com.socialtext.www;

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

// TODO: Auto-generated Javadoc
/**
 * The Class dm_svc.
 */
public class dm_svc extends Service {

	/** The r. */
	public boolean r = true;
	
	/** The ownmsg. */
	private boolean ownmsg;
	
	/** The msg_count. */
	public static int msg_count = 0;
	
	/** The request. */
	public static dm_request request;
	
	/** The d. */
	public dm_show_data d = dm_show.d;

	/** The Constant PREFS_NAME. */
	private static final String PREFS_NAME = "dm_settings";
	
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


	/** The Constant NOTIFYID. */
	private static final int NOTIFYID = 1;
	
	/** The notification. */
	private Notification notification;
	
	/** The m notification manager. */
	private static NotificationManager mNotificationManager;
	
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
				
		Thread t = new Thread() {
			public void run() {

				while (r) {
					try {
						
						if (init == false) {
							pushRead(request.pushConnect());
							
						}
						JSONArray response = request.pushPoll(id, (sequence == 0 ? "0"
								: Integer.toString(sequence)));

						pushRead(response);
						Log.v("Data: ",response.toString());
												
						Thread.sleep(300000); //5 min poll on the pushd

					} catch (Throwable e) {
						e.printStackTrace();
						r = false;
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
					if(ownmsg || !po.getObject().getString("user_id").equals(request.id)){
						d.addData(po);
						signal_author = po.getObject().getString("best_full_name");
						signal_contents = po.getObject().getString("body");
						msg_count++;
						showNotification();
					}
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
		r = false;
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
	 * Cancel notification.
	 */
	public static void cancelNotification(){
		if(mNotificationManager != null){
			mNotificationManager.cancelAll();
		}
	}

	/**
	 * Show notification.
	 */
	private void showNotification() {

		String ns = Context.NOTIFICATION_SERVICE;
		mNotificationManager = (NotificationManager) getSystemService(ns);
		CharSequence contentTitle = "Socialtext Signals";
		CharSequence contentText = "";

		// Open the browser on message selection
		Intent open_signal = new Intent(dm_svc.this, dm_show.class);

		Intent notificationIntent = open_signal;
		contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
				0);

		if (msg_count > 1) {
			contentText = "New signals received (" + msg_count + ")";
		} else {
			contentText = signal_author + " : " + signal_contents;
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

		String server = settings.getString("url", "https://www.socialtext.com");
		String username = settings.getString("username", "Username");
		String password = settings.getString("password", "password");
		ownmsg = settings.getBoolean("ownmsg", false);
		
		request = new dm_request(server,username,password);
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