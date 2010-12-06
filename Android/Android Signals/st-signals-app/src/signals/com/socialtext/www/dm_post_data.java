package signals.com.socialtext.www;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.graphics.Bitmap;

// TODO: Auto-generated Javadoc
/**
 * The Class dm_post_data.
 */
public class dm_post_data {
	
	/** The Constant BODY. */
	private final static String BODY = "BODY";
	
	/** The Constant ICON. */
	private final static String ICON = "ICON";
	
	/** The pics. */
	private HashMap<String, Bitmap> pics = new HashMap<String, Bitmap>();
	
	/** The signals. */
	private ArrayList<Map<String, Object>> signals = new ArrayList<Map<String, Object>>();
	
	/** The new data. */
	public boolean newData;
	
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
	 * Removes all data.
	 */
	public void removeAllData(){
		signals = new ArrayList<Map<String, Object>>();
	}
	
	/**
	 * Adds the data.
	 *
	 * @param id the id
	 * @param body the body
	 * @param name the name
	 */
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
