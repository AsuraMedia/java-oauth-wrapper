package com.smg.oauth;

import com.smg.oauth.config.OauthConfig;
import com.smg.oauth.http.HttpWrapper;

/**
 * Created by eduardo on 23/01/15.
 */
public class Injector
{
    private static Injector instance = null;
    public Session session = null;
    public OauthConfig config = new OauthConfig();
    public Boolean sslVerification;

    public static Injector getInstance ()
    {
        if (instance == null)
        {
            instance = new Injector();
        }
        return instance;
    }

    public void setInstance (Injector instance)
    {
        this.instance = instance;
    }

    public HttpWrapper getRequest ()
    {
        return new HttpWrapper();
    }

    public Session getSession ()
    {
        return this.session;
    }

    public void setSession (Session session)
    {
        this.session = session;
    }

    public OauthConfig getConfig ()
    {
        return config;
    }

    public void setConfig (OauthConfig config)
    {
        this.config = config;
    }

    public Boolean getSslVerification ()
    {
        return sslVerification;
    }

    public void setSslVerification (Boolean sslVerification)
    {
        this.sslVerification = sslVerification;
    }
}
