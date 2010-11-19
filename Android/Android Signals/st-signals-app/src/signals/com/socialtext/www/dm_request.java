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

public class dm_request{
	
	public enum Mimetype {
		TEXT("text/plain"), HTML("text/html"), JSON("application/json"), WIKI(
				"application/x-socialtext-wiki");

		private String m_mimetype;

		private Mimetype(String type) {
			m_mimetype = type;
		}

		public String getType() {
			return m_mimetype;
		}
	}

	public enum Method {
		GET, PUT, POST, DELETE;
	}
	
	public String server;
	public String username;
	public String password;
	public String id;
	public CookieStore cookies;
	private Mimetype m_default_mime = Mimetype.JSON; /* JSON by default */
	
	public dm_request(final String server, final String username, final String password){
		this.server = server;
		this.username = username;
		this.password = password;
		getCookie();
		getUserID();
	}
			
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
     * @param signal The signal object to be posted.
     */
    public void postSignal(String body){
        String path = "/data/signals";
        String sig = "{ \"signal\": \" " + body + "\"}";

        request(path, Method.POST, Mimetype.JSON, Mimetype.JSON, sig, false);
    }
	
    /**
     * Posts a signal to the socialtext stream.
     *
     * @param signal The signal object to be posted.
     */
    public void postSignal(String body, ArrayList<String> groups){
        String path = "/data/signals";
        
        Log.v("Groups: ", groups.toString());
        
        String sig = "{ \"signal\": \" " + body + "\", \"group_ids\":"+ groups +"}";
        request(path, Method.POST, Mimetype.JSON, Mimetype.JSON, sig, false);
    }
    
	/**
     * Private function to actually send the HTTP request.
     * Maybe we need a callback here that is passed as a parameter too?
     *
     * @param url The url to request.
     * @param method The method (GET, POST, PUT, DELETE)
     * @param contenttype The content type (text/html, text/plain,
     *                      text/x.socialtext-wiki, application/json)
     * @param accepttype The type we accept (see contenttype for values)
     * @param data The data that we are sending (if any)
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