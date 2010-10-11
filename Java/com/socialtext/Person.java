package com.socialtext;

import java.util.ArrayList;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Person extends STObject
{
   
    private String m_best_full_name;
    private String m_sort_key;
    private int m_personid;
    
    public Person() { }

    public Person(String json)
    {
        fromJSON(json);
    }

    public int getId()
    {
        return m_personid;
    }

    public void setId(int id)
    {
        m_personid = id;
    }
    
    public String getFullname()
    {
        return m_best_full_name;
    }

    public void setFullname(String name)
    {
        m_best_full_name = name;
    }
    
    public String getSortkey()
    {
        return m_sort_key;
    }

    public void fromJSON(String json)
    {
        try
        {
            JSONObject jobj = new JSONObject(json);
            m_best_full_name = jobj.getString("best_full_name");
            m_personid = jobj.getInt("id");
            m_sort_key = jobj.getString("sort_key");
        }
        catch (JSONException e)
        {
        }
    }
    
    public String toJSON() throws JSONException
    {
        //This should not be used. We cannot create Person
        return null;
    }

    public String toString()
    {
        return String.format("Name: \"%s\"\n\tid: %d\n\tSort key:\"%s\"", m_best_full_name, m_personid, m_sort_key);
    }
}
