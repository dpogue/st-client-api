package com.socialtext;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

public class Signal extends STObject
{
    private String m_body;
    private Date m_date;
    private int m_userid;
    private String m_fullname;
    private String m_uri;
    private String m_hash;

    public Signal() { }

    public Signal(String json)
    {
        fromJSON(json);
    }

    public String getBody()
    {
        return m_body;
    }

    public void setBody(String body)
    {
        m_body = body;
    }

    public void fromJSON(String json)
    {
        try
        {
            JSONObject jobj = new JSONObject(json);
            m_body = jobj.getString("body");
            try
            {
                m_date = new SimpleDateFormat("yyyy-MM-dd%20HH:mm:ss").parse(
                            jobj.getString("at"));
            }
            catch (ParseException e)
            {
                m_date = new Date();
            }
            m_userid = jobj.getInt("user_id");
            m_fullname = jobj.getString("best_full_name");
            m_uri = jobj.getString("uri");
            m_hash = jobj.getString("hash");
        }
        catch (JSONException e)
        {
        }
    }

    public String toJSON() throws JSONException
    {
        JSONObject jobj = new JSONObject();
        jobj.put("signal", m_body);

        return jobj.toString();
    }

    public String toString()
    {
        return String.format("\"%s\"\n\t-- %s (%s)", m_body, m_fullname,
                    new SimpleDateFormat("HH:mm:ss dd/MM/yyyy").format(m_date));
    }
}
