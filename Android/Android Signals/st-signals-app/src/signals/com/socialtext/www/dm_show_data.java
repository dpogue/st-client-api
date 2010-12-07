package signals.com.socialtext.www;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;

import android.graphics.Bitmap;

// TODO: Auto-generated Javadoc
/**
 * The Class dm_show_data.
 */
public class dm_show_data {
	
	/** The Constant BODY. */
	private final static String BODY = "BODY";
	
	/** The Constant NAME. */
	private final static String NAME = "NAME";
	
	/** The Constant ICON. */
	private final static String ICON = "ICON";
	
	/** The Constant ID. */
	private final static String ID = "ID";
	
	/** The Constant AUTH_ID. */
	private final static String AUTH_ID = "AUTH_ID";
	
	/** The Constant TIME. */
	private final static String TIME = "TIME";
	
	/** The Constant REPLY. */
	private final static String REPLY = "REPLY";
	
	/** The pics. */
	private HashMap<String, Bitmap> pics = new HashMap<String, Bitmap>();
	
	/** The signals. */
	private ArrayList<Map<String, Object>> signals = new ArrayList<Map<String, Object>>();
	
	/** The signal_author. */
	private String signal_author; 
	
	/** The signal_contents. */
	private String signal_contents;
	
	/** The signal_auth_id. */
	private String signal_auth_id;
	
	/** The signal_id. */
	private String signal_id;
	
	/** The signal_at. */
	private String signal_at;
	
	/** The signal_replyto. */
	private String signal_replyto;
	
	/** The new data. */
	public boolean newData;
	
	/**
	 * Instantiates a new dm_show_data.
	 */
	public dm_show_data(){
		
		init_data();
		
	}
	
	/**
	 * Init_data.
	 */
	private void init_data(){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(BODY, "Compose new signal");
		map.put(NAME, "New Signal");
		map.put(ICON, R.drawable.ic_menu_edit);
		map.put(ID, "");
		map.put(TIME, "");
		signals.add(map);
		newData = true;
	}
	
	/**
	 * Gets the data.
	 *
	 * @return the data
	 */
	public ArrayList<Map<String, Object>> getData(){
		newData = false;
		return signals;
	}
	
	/**
	 * Gets the signal.
	 *
	 * @param id the id
	 * @return the signal
	 */
	public Map<String, Object> getSignal(int id){
		
		return signals.get(id);
	}
	
	/**
	 * Gets the icon.
	 *
	 * @param id the id
	 * @return the icon
	 */
	public Bitmap getIcon(String id){
		return pics.get(id);
	}
	
	/**
	 * Removes the data.
	 *
	 * @param d the d
	 */
	public void removeData(Map<String,Object> d){
		signals.remove(d);
	}
	
	/**
	 * Removes the all data.
	 */
	public void removeAllData(){
		signals = new ArrayList<Map<String, Object>>();
		init_data();
	}
	
	/**
	 * Adds the data.
	 *
	 * @param po the po
	 */
	public void addData(PushObject po){

		try {
			signal_author = po.getObject().getString("best_full_name");
			signal_contents = po.getObject().getString("body");
			signal_auth_id = po.getObject().getString("user_id");
			signal_id = po.getObject().getString("signal_id");
			signal_at = po.getObject().getString("at");
			signal_replyto = po.getObject().optString("in_reply_to");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(BODY, signal_contents);
		map.put(NAME, signal_author);
		map.put(AUTH_ID, signal_auth_id);
		map.put(ID, signal_id);
		map.put(TIME, signal_at);
		
		if(signal_replyto != null){
			map.put(REPLY,signal_replyto);
		}
		
		if(!pics.containsKey(signal_auth_id)){
			pics.put(signal_auth_id, dm_svc.request.downloadFile(signal_auth_id));
		}
		
		map.put(ICON, pics.get(signal_auth_id));
		
					
		signals.add(map);		
		newData = true;
		
	}	

}
