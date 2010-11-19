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

public class dm_svc extends Service {

	public boolean r = true;
	private boolean ownmsg;
	public static int msg_count = 0;
	public static dm_request request;
	public dm_show_data d = dm_show.d;

	
	private static final String PREFS_NAME = "dm_settings";
	private String id;
	private int sequence = 0;
	private boolean init = false;
	private String signal_contents;
	private String signal_author;


	private static final int NOTIFYID = 1;
	private Notification notification;
	private static NotificationManager mNotificationManager;
	private PendingIntent contentIntent;
	
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
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		r = false;
	}

	private void setupNotification() {

		notification = new Notification(R.drawable.notify_signal,
				"New Signal Received", System.currentTimeMillis());

		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.defaults |= Notification.DEFAULT_ALL;

	}
	
	public static void cancelNotification(){
		if(mNotificationManager != null){
			mNotificationManager.cancelAll();
		}
	}

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
	
	private void getSettings() {

		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

		String server = settings.getString("url", "https://www.socialtext.com");
		String username = settings.getString("username", "Username");
		String password = settings.getString("password", "password");
		ownmsg = settings.getBoolean("ownmsg", false);
		
		request = new dm_request(server,username,password);
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
}