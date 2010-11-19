package signals.com.socialtext.www;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.graphics.Bitmap;

public class dm_post_data {
	
	private final static String BODY = "BODY";
	private final static String ICON = "ICON";
	
	private HashMap<String, Bitmap> pics = new HashMap<String, Bitmap>();
	private ArrayList<Map<String, Object>> signals = new ArrayList<Map<String, Object>>();
	
	public boolean newData;
	
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
	}
	
	public void addData(String id, String body, String name){
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(BODY, name+": " + body);
		
		if(!pics.containsKey(id)){
			pics.put(id, dm_svc.request.downloadFile(id));
		}
		
		map.put(ICON, pics.get(id));
		
		signals.add(map);		
		newData = true;
		
	}	

}
