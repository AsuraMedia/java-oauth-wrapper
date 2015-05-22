package com.smg.oauth.config;

/**
 * Created by eduardo on 14/04/15.
 */
public class OauthOptions
{
    String code;
    String credentials;
    Boolean forceRefresh;

    public String getCode ()
    {
        return code;
    }

    public void setCode (String code)
    {
        this.code = code;
    }

    public String getCredentials ()
    {
        return credentials;
    }

    public void setCredentials (String credentials)
    {
        this.credentials = credentials;
    }

    public Boolean isForceRefresh ()
    {
        return forceRefresh;
    }

    public void setForceRefresh (Boolean forceRefresh)
    {
        this.forceRefresh = forceRefresh;
    }
}
