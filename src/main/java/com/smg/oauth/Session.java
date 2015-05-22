package com.smg.oauth;

/**
 * Created by eduardo on 29/01/15.
 */
public class Session
{
    public OAuthIO oauthIO;

    public OAuthIO getOauthIO ()
    {
        return oauthIO;
    }

    public void setOauthIO (OAuthIO oauthIO)
    {
        this.oauthIO = oauthIO;
    }
}
