package signals.com.socialtext.www;

import org.json.JSONException;
import org.json.JSONObject;

public class PushObject
{
    private String m_class;
    private JSONObject m_object;

    public PushObject(String json)
    {
        fromJSON(json);
    }

    public String getObjClass()
    {
        return m_class;
    }

    public JSONObject getObject()
    {
        return m_object;
    }

    public void fromJSON(String json)
    {
        try
        {
            JSONObject jobj = new JSONObject(json);
            m_class = jobj.getString("class");
            m_object = jobj.getJSONObject("object");
        }
        catch (JSONException e)
        {
        }
    }
    
    public String toJSON() throws JSONException
    {
        throw new JSONException("Cannot create PushObject instances");
    }
}
