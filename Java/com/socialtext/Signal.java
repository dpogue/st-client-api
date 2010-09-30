package com.socialtext;

import java.util.Date;

public class Signal extends STObject
{
    private String m_body;
    private Date m_date;
    private String m_userid; /* Int? Are user IDs guaranteed to be integers? */

    public Signal() { }

    public Signal(String json)
    {
        fromJSON(json);
    }

    public void fromJSON(String json)
    {
    }

    public String toJSON()
    {
        return "{body: \"" + m_body + "\"}";
    }
}
