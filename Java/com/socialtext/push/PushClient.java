package com.socialtext.push;

import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.cookie.Cookie;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

public class PushClient
{
    private String m_site_url;
    private String m_username;
    private String m_password;
    private Cookie m_push_cookie;
    private int m_sequence;
    private String m_client_id;
    private boolean m_init = false;

    public PushClient(String url, String username, String password)
    {
        m_site_url = url;
        m_username = username;
        m_password = password;
    }

    public ArrayList<PushObject> fetch()
    {
        if (!m_init) {
            init();
        }

        if (m_init) {
            try
            {
                String req = request("client_id=" + m_client_id + ";sequence=" + m_sequence);
                JSONArray cmds = new JSONArray(req);
                ArrayList<PushObject> objs = new ArrayList<PushObject>();

                for (int i = 0; i < cmds.length(); i++) {
                    PushObject po = new PushObject(cmds.getJSONObject(i).toString());

                    if (po.getObjClass().equals("command")) {
                        String comm = po.getObject().getString("command");
                        if (comm.equals("continue")) {
                            m_sequence = po.getObject().optInt("sequence", m_sequence);
                        } else if (comm.equals("goodbye")) {
                            m_init = false;
                        }
                    } else if (po.getObjClass().equals("signal")) {
                        objs.add(po);
                    } else if (po.getObjClass().equals("hide_signal")) {
                        objs.add(po);
                    }
                }

                return objs;
            }
            catch (JSONException e)
            {
                return null;
            }
        }

        return null;
    }

    private void init()
    {
        if (getPushCookie()) {
            try
            {
                String welcome = request("nowait=1");
                JSONArray cmds = new JSONArray(welcome);

                for (int i = 0; i < cmds.length(); i++) {
                    PushObject po = new PushObject(cmds.getJSONObject(i).toString());

                    if (po.getObjClass().equals("command")) {
                        String comm = po.getObject().getString("command");
                        if (comm.equals("welcome")) {
                            m_client_id = po.getObject().getString("client_id");
                            m_init = true;
                        } else if (comm.equals("goodbye")) {
                            m_init = false;
                        } else if (comm.equals("continue")) {
                            m_sequence = po.getObject().getInt("sequence");
                        } else {
                            /* Unhandled Command! */
                        }
                    }
                }
            }
            catch (JSONException e)
            {
            }
        }
    }

    private boolean getPushCookie() {
        URI uri;
        try
        {
            uri = new URI(m_site_url + "/nlw/submit/login");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return false;
        }
        HttpParams params = new BasicHttpParams();
        params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
                HttpVersion.HTTP_1_1);
        params.setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, false);
        DefaultHttpClient httpclient = new DefaultHttpClient(params);

        HttpResponse response;
        HttpPost httpreq = new HttpPost(uri);
        httpreq.setHeader("Content-Type", "application/x-www-form-urlencoded");

        try
        {
            ArrayList<NameValuePair> formPairs = new ArrayList<NameValuePair>(3);
            formPairs.add(new BasicNameValuePair("username", m_username));
            formPairs.add(new BasicNameValuePair("password", m_password));
            formPairs.add(new BasicNameValuePair("remember", "1"));
            httpreq.setEntity(new UrlEncodedFormEntity(formPairs, HTTP.UTF_8));
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
            return false;
        }

        try {
            response = httpclient.execute(httpreq);

            if (response.getStatusLine().getStatusCode() == 302) {
                CookieStore cookies = httpclient.getCookieStore();

                for (Cookie c : cookies.getCookies()) {
                    if (c.getName().equals("NLW-user")) {
                        m_push_cookie = c;
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return false;
    }

    /**
     * Private function to actually send the HTTP request.
     *
     * @params req_params The GET parameters to pass along with the request.
     */
    private String request(String req_params)
    {
        URI uri;
        try
        {
            uri = new URI(m_site_url + "/data/push?" + req_params);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return e.toString();
        }
        HttpParams params = new BasicHttpParams();
        params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
                HttpVersion.HTTP_1_1);
        DefaultHttpClient httpclient = new DefaultHttpClient(params);

        HttpResponse response;
        HttpGet httpreq = new HttpGet(uri);
        httpreq.setHeader("Accept", "application/json");

        httpclient.getCookieStore().addCookie(m_push_cookie);

        try
        {
            response = httpclient.execute(httpreq);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                entity.writeTo(os);
                String result = os.toString("UTF8");

                return result;
            }
            return entity.toString();
        }
        catch (Throwable e)
        {
            /* Error handling? What Error handling? :D */
            e.printStackTrace();
            return e.toString();
        }
    }
}
