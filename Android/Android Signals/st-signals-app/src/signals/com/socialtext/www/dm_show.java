package signals.com.socialtext.www;


import java.util.ArrayList;
import java.util.Map;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;

public class dm_show extends ListActivity {
	
	private static final String PREFS_NAME = "dm_settings";

	private static final String NAME = "NAME";
	private static final String BODY = "BODY";
	private static final String ICON = "ICON";

	private SimpleAdapter adapter;
	public static dm_show_data d = new dm_show_data();
	private boolean open_first;
	private boolean activityOpen;
	private static Intent serviceID = null;
	final Handler mHandler = new Handler();
	ArrayList<Map<String, Object>> drawables;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getSettings(); // update settings if they've changed
		activityOpen = true;
		init_listview();
		
		if(!open_first){
			startActivity(new Intent(this, dm_settings.class));
		}

		start_Service();		
		checkNewData(); //Fire off a thread to check for new data
		
	}
	
	private void start_Service(){
		
		if(serviceID != null){
			stopService(serviceID);
		}
		serviceID = new Intent(this, dm_svc.class);
		startService(serviceID);
	}
	
	private void init_listview(){
		
		drawables = d.getData();

		// Now build the list adapter
		adapter = new SimpleAdapter(
		// the Context
				this,
				// the data to display
				drawables,
				// The layout to use for each item
				R.layout.dm_show_row,
				// The list item attributes to display
				new String[] { ICON, NAME, BODY},
				// And the ids of the views where they should be displayed (same
				// order)
				new int[] { R.id.icon, R.id.text_name, R.id.text_body});

		adapter.setViewBinder(new MyViewBinder());
		setListAdapter(adapter);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.dm_show_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
		    case R.id.menu_settings:
		    	startActivity(new Intent(this, dm_settings.class));
		    	getSettings();
		        return true;
		    case R.id.menu_clear:
		    	d.removeAllData();
		    	init_listview();
		    	return true;
		    default:
		        return super.onOptionsItemSelected(item);
	    }
	}
	
	//Every 3 seconds check for new data and cancel notifications
	private void checkNewData() {
		
        Thread t = new Thread() {
            public void run() {
            	while(true){
	                if(d.newData){
	                	mHandler.post(mUpdateResults);
	                	if(activityOpen){
	                		dm_svc.cancelNotification();
	                	}
	                }
                	try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
            	}
            }
        };
        t.start();
    }
		
	final Runnable mUpdateResults = new Runnable() {
        public void run() {
        	updateResultsInUi();
        }
    };
    
    private void updateResultsInUi() {
    	adapter.notifyDataSetChanged();
    }
    
    @Override
    public void onPause(){
    	super.onPause();
    	activityOpen = false;
    }
    
    @Override
	public void onResume(){
		super.onResume();
		dm_svc.msg_count=0;
		activityOpen = true;
		dm_svc.cancelNotification();
	}
	
	public void onDestroy(){
		super.onDestroy();
	}

	public class MyViewBinder implements ViewBinder {
		@Override
		public boolean setViewValue(View view, Object data,
				String textRepresentation) {
			if ((view instanceof ImageView) & (data instanceof Bitmap)) {
				ImageView iv = (ImageView) view;
				Bitmap bm = (Bitmap) data;
				iv.setImageBitmap(bm);
				return true;
			}
			return false;
		}
	}

	private void getSettings() {

		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

		open_first = settings.getBoolean("open_first", false);
	}

	public void onListItemClick(ListView parent, View v, int position, long id) {
		switch(position){
			default:
				Intent i = new Intent(this, dm_post.class);
				i.putExtra("reply_to", position);
				startActivity(i);
				break;
		}
	}

}