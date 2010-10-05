package com.socialtext;

import org.json.JSONException;

public abstract class STObject
{
    /**
     * Initialises class members from a string of JSON.
     *
     * @param json The string of JSON to parse.
     */
    public abstract void fromJSON(String json);

    /**
     * Returns the JSON representation of the instance members.
     *
     * @return JSON representation as a string.
     * @throws JSONException
     */
    public abstract String toJSON() throws JSONException;
}
