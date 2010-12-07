package signals.com.socialtext.www;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
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
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

// TODO: Auto-generated Javadoc
/**
 * The Class dm_request.
 */
public class dm_request{
	
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
	
	/** The server. */
	public String server;
	
	/** The username. */
	public String username;
	
	/** The password. */
	public String password;
	
	/** The id. */
	public String id;
	
	/** The cookies. */
	public CookieStore cookies;
	
	/** The m_default_mime. */
	private Mimetype m_default_mime = Mimetype.JSON; /* JSON by default */
	
	/**
	 * Instantiates a new dm_request.
	 *
	 * @param server the server
	 * @param username the username
	 * @param password the password
	 */
	public dm_request(final String server, final String username, final String password){
		this.server = server;
		this.username = username;
		this.password = password;
		getCookie();
		getUserID();
	}
			
	/**
	 * Gets the cookie.
	 *
	 * @return the cookie
	 */
	public void getCookie() {

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
				cookies =  httpclient.getCookieStore();
			}

		} catch (IOException e) {
			System.err.println(e.toString());
		}
	}
	
	/**
	 * Gets the user id.
	 *
	 * @return the user id
	 */
	public void getUserID(){
		String path = "/data/people/"+username;
		String json = request(path, Method.GET, Mimetype.JSON, Mimetype.JSON, null, true);
				
		try {
			JSONObject data = new JSONObject(json);
							
			String user_id = data.optString("id");
			if(user_id != null){
				id = user_id;
				
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
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
				null,true);
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
		String path = "/data/push?nowait=1&client_id=" + id + ";sequence=" + sequence;
		String json = request(path, Method.GET, Mimetype.JSON, Mimetype.JSON,
				null, true);
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
	 * Gets the groups.
	 *
	 * @param id the id
	 * @return the groups
	 */
	public HashMap<String,String> getGroups(String id){
		
		String path = "/data/networks?accept=json";
		String json = request(path, Method.GET, Mimetype.JSON, Mimetype.JSON, null, true);
		
		HashMap<String,String> groups = new HashMap<String,String>();
		groups.put("Default","-1");
		
		try {
			JSONArray data = new JSONArray(json);
							
			for (int i = 0; i < data.length(); i++) {
				JSONObject po = data.getJSONObject(i);
				
				String type = po.optString("type");
				if(type.equals("group")){
					String group_name = po.optString("name");
					String group_id = po.optString("id");
					if(group_id != null && group_name != null){
						groups.put(group_name,group_id);
					}
				}
			}
			
			return groups;
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
    /**
     * Posts a signal to the socialtext stream.
     *
     * @param body the body
     */
    public void postSignal(String body){
        String path = "/data/signals";
        String sig = "{ \"signal\":\"" + body + "\"}";

        request(path, Method.POST, Mimetype.JSON, Mimetype.JSON, sig, false);
    }
    
    /**
     * Post reply.
     *
     * @param body the body
     * @param reply_to the reply_to
     */
    public void postReply(String body, String reply_to){
        String path = "/data/signals";
        String reply = "{ \"signal_id\":\"" + reply_to + "\"}";
        String sig = "{ \"signal\": \"" + body + "\",\"in_reply_to\":" + reply + "}";

        request(path, Method.POST, Mimetype.JSON, Mimetype.JSON, sig, false);
    }
	
    /**
     * Posts a signal to the socialtext stream.
     *
     * @param body the body
     * @param groups the groups
     */
    public void postSignal(String body, ArrayList<String> groups){
        String path = "/data/signals";
        
        Log.v("Groups: ", groups.toString());
        
        String sig = "{ \"signal\":\"" + body + "\", \"group_ids\":"+ groups +"}";
        request(path, Method.POST, Mimetype.JSON, Mimetype.JSON, sig, false);
    }
    
	/**
	 * Private function to actually send the HTTP request.
	 * Maybe we need a callback here that is passed as a parameter too?
	 *
	 * @param url The url to request.
	 * @param method The method (GET, POST, PUT, DELETE)
	 * @param contenttype The content type (text/html, text/plain,
	 * text/x.socialtext-wiki, application/json)
	 * @param accepttype The type we accept (see contenttype for values)
	 * @param data The data that we are sending (if any)
	 * @param useCookie the use cookie
	 * @return the string
	 */
    private String request(String url, Method method, Mimetype contenttype,
                            Mimetype accepttype, String data, boolean useCookie)
    {
        accepttype = (accepttype != null) ? accepttype : m_default_mime;
        contenttype = (contenttype != null) ? contenttype : m_default_mime;

        URI uri;
        try
        {
            uri = new URI(server + url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return e.toString();
        }
        HttpParams params = new BasicHttpParams();
        params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
                HttpVersion.HTTP_1_1);
        DefaultHttpClient httpclient = new DefaultHttpClient(params);

        HttpResponse response;
        HttpRequestBase httpreq;
        if (method == Method.POST)
        {
            httpreq = new HttpPost(uri);
            try
            {
                StringEntity postbody = new StringEntity(data, HTTP.UTF_8);
                ((HttpPost)httpreq).setEntity(postbody);
            }
            catch (UnsupportedEncodingException e)
            {
                e.printStackTrace();
                return e.toString();
            }
        }
        else
        {
            /* Default to GET */
            httpreq = new HttpGet(uri);
        }
        httpreq.setHeader("Accept", accepttype.getType());
        httpreq.setHeader("Content-Type", contenttype.getType());

        if(useCookie){
        	httpclient.setCookieStore(cookies); // set the cookie store
        }
        else{
	        httpclient.getCredentialsProvider().setCredentials(
	                new AuthScope(null, -1),
	                new UsernamePasswordCredentials(username, password));
        }

        try {
			response = httpclient.execute(httpreq);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                entity.writeTo(os);
                String result = os.toString();

                return result;
            }
            return null;
            
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return e.toString();
		} catch (IOException e) {
			e.printStackTrace();
			return e.toString();
		}
    }
    
    /**
     * Download file.
     *
     * @param id the id
     * @return the bitmap
     */
    public Bitmap downloadFile(String id) {
		URI uri = null;

		try {
			uri = new URI(server + "/data/people/" + id + "/photo");
			HttpParams params = new BasicHttpParams();
			params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
					HttpVersion.HTTP_1_1);
			DefaultHttpClient httpclient = new DefaultHttpClient(params);

			httpclient.getCredentialsProvider().setCredentials(
					new AuthScope(null, -1),
					new UsernamePasswordCredentials(username, password));

			HttpResponse response;
			HttpRequestBase httpreq;

			httpreq = new HttpGet(uri);
			response = httpclient.execute(httpreq);

			return BitmapFactory
					.decodeStream(response.getEntity().getContent());

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
		
}