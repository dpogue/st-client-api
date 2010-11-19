package com.socialtext;

import java.util.ArrayList;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Signal extends STObject
{
    public class Annotation
    {
        private String m_type;
        private JSONObject m_data; // There are no rules...

        public Annotation() { }

        public Annotation(String type)
        {
            m_type = type;
        }

        public String getType()
        {
            return m_type;
        }

        public void setType(String type)
        {
            m_type = type;
        }

        public JSONObject getData()
        {
            return m_data;
        }

        public void setData(JSONObject data)
        {
            m_data = data;
        }
    }

    private String m_body;
    private Date m_date;
    private int m_signalid;
    private Person m_sender;
    private String m_uri;
    private String m_hash;
    private Signal m_reply_to;
    //private Person m_recipient;
    private ArrayList<Annotation> m_annotations;
    private ArrayList<String> m_tags; // Tags class?

    public Signal() { }

    /** creates signal constructor from json object */
    public Signal(String json)
    {
        fromJSON(json);
    }

    /** gets body of signal */
    public String getBody()
    {
        return m_body;
    }

    /** sets body of signal */
    public void setBody(String body)
    {
        m_body = body;
    }

    /** sets id of signal */
    public int getID()
    {
        return m_signalid;
    }

    /** sets reply to id */
    public int getReplyID()
    {
        if (m_reply_to != null)
        {
            return m_reply_to.getID();
        }
        else
        {
            return -1;
        }
    }

    /** sets Reply of signal */
    public void setReply(Signal reply)
    {
        m_reply_to = reply;
    }

    /** Sets all signal info from json object */
    public void fromJSON(String json)
    {
        try
        {
            JSONObject jobj = new JSONObject(json);
            m_signalid = jobj.getInt("signal_id");
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
            m_sender = new Person();
            m_sender.setId(jobj.getInt("user_id"));
            m_sender.setFullname(jobj.getString("best_full_name"));
            m_uri = jobj.getString("uri");
            m_hash = jobj.getString("hash");

            if (jobj.optJSONObject("in_reply_to") != null)
            {
                m_reply_to = new Signal();
                m_reply_to.m_signalid = jobj.getJSONObject("in_reply_to").getInt("signal_id");
                m_reply_to.m_uri = jobj.getJSONObject("in_reply_to").getString("uri");
            }

            m_annotations = new ArrayList<Annotation>();
            JSONArray anns = jobj.optJSONArray("annotations");
        }
        catch (JSONException e)
        {
        }
    }

    /** sets json object */
    public String toJSON() throws JSONException
    {
        JSONObject jobj = new JSONObject();
        jobj.put("signal", m_body);

        if (m_reply_to != null)
        {
            JSONObject reply = new JSONObject();
            reply.put("signal_id", m_reply_to.getID());
            jobj.put("in_reply_to", reply);
        }

        return jobj.toString();
    }

    /** prints out all signal info */
    public String toString()
    {
        return String.format("\"%s\"\n\t-- %s (%s)", m_body, m_sender.getFullname(),
                    new SimpleDateFormat("HH:mm:ss dd/MM/yyyy").format(m_date));
    }
}
