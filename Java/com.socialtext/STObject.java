package com.socialtext;

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
     */
    public abstract String toJSON();
}
