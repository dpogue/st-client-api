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

// TODO: Auto-generated Javadoc
/**
 * The Class dm_show.
 */
public class dm_show extends ListActivity {
	
	/** The Constant PREFS_NAME. */
	private static final String PREFS_NAME = "dm_settings";

	/** The Constant NAME. */
	private static final String NAME = "NAME";
	
	/** The Constant BODY. */
	private static final String BODY = "BODY";
	
	/** The Constant ICON. */
	private static final String ICON = "ICON";

	/** The adapter. */
	private SimpleAdapter adapter;
	
	/** The d. */
	public static dm_show_data d = new dm_show_data();
	
	/** The open_first. */
	private boolean open_first;
	
	/** The activity open. */
	private boolean activityOpen;
	
	/** The service id. */
	private static Intent serviceID = null;
	
	/** The m handler. */
	final Handler mHandler = new Handler();
	
	/** The drawables. */
	ArrayList<Map<String, Object>> drawables;
	

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
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
	
	/**
	 * Start_ service.
	 */
	private void start_Service(){
		
		if(serviceID != null){
			stopService(serviceID);
		}
		serviceID = new Intent(this, dm_svc.class);
		startService(serviceID);
	}
	
	/**
	 * Init_listview.
	 */
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
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.dm_show_menu, menu);
	    return true;
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
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
	/**
	 * Check new data.
	 */
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
		
	/** The m update results. */
	final Runnable mUpdateResults = new Runnable() {
        public void run() {
        	updateResultsInUi();
        }
    };
    
    /**
     * Update results in ui.
     */
    private void updateResultsInUi() {
    	adapter.notifyDataSetChanged();
    }
    
    /* (non-Javadoc)
     * @see android.app.Activity#onPause()
     */
    @Override
    public void onPause(){
    	super.onPause();
    	activityOpen = false;
    }
    
    /* (non-Javadoc)
     * @see android.app.Activity#onResume()
     */
    @Override
	public void onResume(){
		super.onResume();
		dm_svc.msg_count=0;
		activityOpen = true;
		dm_svc.cancelNotification();
	}
	
	/* (non-Javadoc)
	 * @see android.app.ListActivity#onDestroy()
	 */
	public void onDestroy(){
		super.onDestroy();
	}

	/**
	 * The Class MyViewBinder.
	 */
	public class MyViewBinder implements ViewBinder {
		
		/* (non-Javadoc)
		 * @see android.widget.SimpleAdapter.ViewBinder#setViewValue(android.view.View, java.lang.Object, java.lang.String)
		 */
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

	/**
	 * Gets the settings.
	 *
	 * @return the settings
	 */
	private void getSettings() {

		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

		open_first = settings.getBoolean("open_first", false);
	}

	/* (non-Javadoc)
	 * @see android.app.ListActivity#onListItemClick(android.widget.ListView, android.view.View, int, long)
	 */
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