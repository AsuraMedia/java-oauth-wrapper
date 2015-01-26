package com.smg.oauth;

/**
 * Created by eduardo on 23/01/15.
 */
public class OauthConfig
{
    String oauthUrl = "http://localhost:6284";
    String oauthdBase = "/auth";
    String appKey = "";
    String appSecret = "";

    public OauthConfig(String oautdhUrl, String oauthdBase, String appKey, String appSecret)
    {
        this.oauthUrl = oautdhUrl;
        this.oauthdBase = oauthdBase;
        this.appKey = appKey;
        this.appSecret = appSecret;
    }

    public OauthConfig()
    {
    }

    public OauthConfig(String appKey, String appSecret)
    {
        this.appKey = appKey;
        this.appSecret = appSecret;
    }

    public String getOauthUrl()
    {
        return oauthUrl;
    }

    public void setOauthUrl(String oauthUrl)
    {
        this.oauthUrl = oauthUrl;
    }

    public String getOauthdBase()
    {
        return oauthdBase;
    }

    public void setOauthdBase(String oauthdBase)
    {
        this.oauthdBase = oauthdBase;
    }

    public String getAppKey()
    {
        return appKey;
    }

    public void setAppKey(String appKey)
    {
        this.appKey = appKey;
    }

    public String getAppSecret()
    {
        return appSecret;
    }

    public void setAppSecret(String appSecret)
    {
        this.appSecret = appSecret;
    }
}
