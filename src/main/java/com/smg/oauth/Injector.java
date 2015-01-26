package com.smg.oauth;

import java.util.Map;

/**
 * Created by eduardo on 23/01/15.
 */
public class Injector
{
    public Map<String,Object> session=null;
    public OauthConfig config = new OauthConfig();

    public Boolean sslVerification;

    private static Injector instance = null;

    public static Injector getInstance()
    {
        if(instance == null)
        {
            instance = new Injector();
        }
        return instance;
    }

    public void setInstance(Injector instance)
    {
        this.instance = instance;
    }

    public HttpWrapper getRequest()
    {
        return new HttpWrapper();
    }

    public Object getSession()
    {
        return this.session;
    }
    public void setSession(Map<String,Object>  session)
    {
        this.session = session;
    }

}
