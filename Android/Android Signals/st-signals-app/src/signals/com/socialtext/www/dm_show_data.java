package signals.com.socialtext.www;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;

import android.graphics.Bitmap;

public class dm_show_data {
	
	private final static String BODY = "BODY";
	private final static String NAME = "NAME";
	private final static String ICON = "ICON";
	private final static String ID = "ID";
	private final static String AUTH_ID = "AUTH_ID";
	private final static String TIME = "TIME";
	private final static String REPLY = "REPLY";
	
	
	private HashMap<String, Bitmap> pics = new HashMap<String, Bitmap>();
	private ArrayList<Map<String, Object>> signals = new ArrayList<Map<String, Object>>();
	
	private String signal_author; 
	private String signal_contents;
	private String signal_auth_id;
	private String signal_id;
	private String signal_at;
	private String signal_replyto;
	public boolean newData;
	
	public dm_show_data(){
		
		init_data();
		
	}
	
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
	
	public ArrayList<Map<String, Object>> getData(){
		newData = false;
		return signals;
	}
	
	public Bitmap getIcon(String id){
		return pics.get(id);
	}
	
	public void removeData(Map<String,Object> d){
		signals.remove(d);
	}
	
	public void removeAllData(){
		signals = new ArrayList<Map<String, Object>>();
		init_data();
	}
	
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
