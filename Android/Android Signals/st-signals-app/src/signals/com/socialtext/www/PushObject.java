package signals.com.socialtext.www;

import org.json.JSONException;
import org.json.JSONObject;

// TODO: Auto-generated Javadoc
/**
 * The Class PushObject.
 */
public class PushObject
{
    
    /** The m_class. */
    private String m_class;
    
    /** The m_object. */
    private JSONObject m_object;

    /**
     * Instantiates a new push object.
     *
     * @param json the json
     */
    public PushObject(String json)
    {
        fromJSON(json);
    }

    /**
     * Gets the obj class.
     *
     * @return the obj class
     */
    public String getObjClass()
    {
        return m_class;
    }

    /**
     * Gets the object.
     *
     * @return the object
     */
    public JSONObject getObject()
    {
        return m_object;
    }

    /**
     * From json.
     *
     * @param json the json
     */
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
    
    /**
     * To json.
     *
     * @return the string
     * @throws JSONException the jSON exception
     */
    public String toJSON() throws JSONException
    {
        throw new JSONException("Cannot create PushObject instances");
    }
}
