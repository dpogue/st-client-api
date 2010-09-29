package com.socialtext;

public class Resting
{
    public enum Route
    {
        PAGE("/data/workspaces/%s/pages/%s"),
        PAGES("/data/workspaces/%s/pages"),
        PAGETAG("/data/workspaces/%s/pages/%s/tags/%s"),
        PAGECOMMENTS("/data/workspaces/%s/pages/%s/comments"),
        PAGEATTACHMENT("/data/workspaces/%s/pages/%s/attachments/%s"),
        PAGEATTACHMENTS("/data/workspaces/%s/pages/%s/attachments"),
        PEOPLE("/data/people"),
        PERSON("/data/person/%s"),
        SIGNALS("/data/signals"),
        TAGGEDPAGES("/data/workspaces/%s/tags/%s/pages"),
        WORKSPACE("/data/workspaces/%s"),
        WORKSPACES("/data/workspaces"),
        WORKSPACETAG("/data/workspaces/%s/tags/%s"),
        WORKSPACETAGS("/data/workspaces/%s/tags"),
        WORKSPACEATTACHMENT("/data/workspaces/%s/attachments/%s"),
        WORKSPACEATTACHMENTS("/data/workspaces/%s/attachments"),
        WORKSPACEUSER("/data/workspaces/%s/users/%s"),
        WORKSPACEUSERS("/data/workspaces/%s/users"),
        USER("/data/users/%s"),
        USERS("/data/users");

        private String m_path;

        private Route(String path)
        {
            m_path = path;
        }

        public String getPath()
        {
            return m_path;
        }

        public static String getRoute(String name)
            throws IllegalArgumentException
        {
            Route r = Route.valueOf(name.trim().toUpperCase());
            return r.getPath();
        }
    }

    public enum Mimetype
    {
        TEXT("text/plain"),
        HTML("text/html"),
        JSON("application/json"),
        WIKI("application/x-socialtext-wiki");

        private String m_mimetype;

        private Mimetype(String type)
        {
            m_mimetype = type;
        }

        public String getType()
        {
            return m_mimetype;
        }
    }

    public enum Method
    {
        GET,
        PUT,
        POST,
        DELETE;
    }

    private String m_site_url;
    private String m_username;
    private String m_password;
    private String m_workspace;
    private Mimetype m_accept = Mimetype.JSON; /* JSON by default */

    /**
     * Creates a Socialtext ReST connection.
     *
     * @param url The URL of the connection.
     */
    public Resting(String url)
    {
        m_site_url = url;
        m_username = "";
        m_password = "";
        m_workspace = "";
    }

    /**
     * Creates a Socialtext ReST connection.
     *
     * @param url The URL of the connection.
     * @param username The username for authentication.
     * @param password The password for authnetication.
     */
    public Resting(String url, String username, String password)
    {
        m_site_url = url;
        m_username = username;
        m_password = password;
        m_workspace = "";
    }

    /**
     * Creates a Socialtext ReST connection.
     *
     * @param url The URL of the connection.
     * @param username The username for authentication.
     * @param password The password for authnetication.
     * @param ws The workspace name.
     */
    public Resting(String url, String username, String password, String ws)
    {
        m_site_url = url;
        m_username = username;
        m_password = password;
        m_workspace = ws;
    }

    /**
     * Returns the username of the authenticated user.
     *
     * @return The user's username.
     */
    public String getUsername()
    {
        return m_username;
    }

    /**
     * Sets the username and password for the connection.
     *
     * @param username The username to use for authentication.
     * @param password The password to use for authentication.
     */
    public void setCredentials(String username, String password)
    {
        m_username = username;
        m_password = password;
    }

    /**
     * Gets the current workspace.
     *
     * @return The workspace name.
     */
    public String getWorkspace()
    {
        return m_workspace;
    }

    /**
     * Sets the current workspace.
     *
     * @param workspace The name of the workspace.
     */
    public void setWorkspace(String workspace)
    {
        m_workspace = workspace;
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
    private void request(String url, Method method, Mimetype contenttype,
                            Mimetype accepttype, String data)
    {
        if (accepttype == null)
        {
            accepttype = m_accept;
        }
        /* We send the HTTP request here */
        /* Make sure we auth with username/password! */
    }

    /**
     * Posts a signal to the socialtext stream.
     *
     * @param signal The signal object to be posted.
     */
    public void postSignal(Signal signal)
    {
        String path = m_site_url + Route.getRoute("SIGNALS");

        request(path, Method.POST, Mimetype.JSON, Mimetype.TEXT,
                signal.toJSON());
    }
}
