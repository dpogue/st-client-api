package com.socialtext;

import java.util.Date;
import java.text.SimpleDateFormat;

public class SignalBuilder
{

    public SignalBuilder(){}
  
    public String after(Date after)
    {
	return "after=" + (new SimpleDateFormat("yyyy-MM-dd%20HH:mm:ss").format(after));
    }

    public String before(Date before)
    {
	return "before=" + (new SimpleDateFormat("yyyy-MM-dd%20HH:mm:ss").format(before));
    }

    public String follow()
    {
	return "follow=1";
    }

    public String accounts(int id)
    {
	return "accounts="+Integer.toString(id);
    }

    public String orderDesc ()
    {
	return "direction=desc";
    }

    public String orderAsc ()
    {
	return "direction=asc";
    }

}