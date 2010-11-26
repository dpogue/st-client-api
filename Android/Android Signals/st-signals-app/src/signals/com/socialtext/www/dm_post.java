package signals.com.socialtext.www;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;

public class dm_post extends Activity implements OnClickListener{

	private static final String NAME = "NAME";
	private static final String BODY = "BODY";
	private static final String ICON = "ICON";
	private static final String AUTH_ID = "AUTH_ID";
	private static final String ID = "ID";
	
	private AlertDialog alert;
	private ArrayList<String> groups_dlg;
	public dm_post_data d = new dm_post_data();
	ArrayList<Map<String, Object>> drawables;
	private SimpleAdapter adapter;
	final Handler mHandler = new Handler();
	private int reply_to; 
	private Map<String, Object> reply;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dm_post);
		groups_dlg = new ArrayList<String>();
		
		Button btnPost = (Button) findViewById(R.id.Button);
		btnPost.setOnClickListener(this);
		
		init_listview();

	}
	
	private void init_listview(){
		
		ListView lv = (ListView)findViewById(R.id.dm_post_listview);
		Intent i = this.getIntent();		
		reply_to = i.getIntExtra("reply_to", 0);
		
		if(reply_to > 0){
			reply = dm_show.d.getSignal(reply_to);
			d.addData(reply.get(AUTH_ID).toString(), reply.get(BODY).toString(), reply.get(NAME).toString());
		}
		
		drawables = d.getData();

		// Now build the list adapter
		adapter = new SimpleAdapter(
		// the Context
				this,
				// the data to display
				drawables,
				// The layout to use for each item
				R.layout.dm_post_row,
				// The list item attributes to display
				new String[] { ICON, BODY },
				// And the ids of the views where they should be displayed (same
				// order)
				new int[] { R.id.icon, R.id.text_body});

		adapter.setViewBinder(new MyViewBinder());
		lv.setAdapter(adapter);
		
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

	@Override
	public void onClick(View v) {
				
    	Thread t = new Thread() {
    		public void run() {
    			TextView text_body = (TextView) findViewById(R.id.EditText);
    			String body = text_body.getText().toString();
    			
    			d.addData(dm_svc.request.id, body, "Me");
    			mHandler.post(mUpdateResults);
    			
    			if(reply_to > 0){
    				dm_svc.request.postReply(body, reply.get(ID).toString());
    			}
    			else if(groups_dlg.size() > 0){
    				dm_svc.request.postSignal(body, groups_dlg);
    			}
    			else{
    				dm_svc.request.postSignal(body);
    			}
    		}
    	};

    	t.start();
    	
    	TextView text_body = (TextView) findViewById(R.id.EditText);
    	text_body.setText("");
    	groups_dlg = new ArrayList<String>();
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.dm_post_menu, menu);
	    return true;
	}
	
	private void dialogCreate(){
		
		final HashMap<String,String> groups = dm_svc.request.getGroups(dm_svc.request.id);
		
		final CharSequence[] items = groups.keySet().toArray(new CharSequence[groups.keySet().size()]);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Select a Group");
		builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int item) {
		    	if(item == 0){
		    		groups_dlg = new ArrayList<String>();
		    	}
		    	else{
		    		groups_dlg.add(groups.get(items[item]));
		    	}
		        alert.cancel();
		    }
		});
		
		alert = builder.create();
		alert.show();
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
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
		    case R.id.menu_groups:
		    	dialogCreate();
		        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
}
