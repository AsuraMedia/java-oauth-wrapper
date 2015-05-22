package com.smg.oauth;


import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.smg.oauth.config.*;
import com.smg.oauth.constants.HttpVerbs;
import com.smg.oauth.exceptions.NotAuthenticatedException;
import com.smg.oauth.exceptions.NotInitializedException;
import com.smg.oauth.http.HttpWrapper;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

/**
 * Created by eduardo on 23/01/15.
 */
public class OAuth
{
    public com.smg.oauth.config.Properties properties = new com.smg.oauth.config.Properties();

    private Injector injector;
    private boolean initialized = false;

    public OAuth ()
    {
        this.injector = Injector.getInstance();
    }

    public void setSslVerification (Boolean sslVerification)
    {
        this.injector.sslVerification = sslVerification;
    }

    public void setSession (Session session)
    {
        this.injector.session = session;
    }

    public String getOAuthUrl ()
    {
        return this.injector.config.getOauthUrl();
    }

    public void setOAuthUrl (String url, String base)
    {
        this.injector.config.setOauthUrl(url);

        if (base != null && !base.isEmpty() && !base.startsWith("/"))
        {
            base = "/" + base;
        }
        if (base != null && base.equalsIgnoreCase("/"))
        {
            base = "";
        }

        this.injector.config.setOauthdBase(base);
    }

    public void initialize (String key, String secret)
    {
        this.injector.config.setAppKey(key);
        this.injector.config.setAppSecret(secret);
        this.initSession();
        this.initialized = true;

    }

    public String getAppKey ()
    {
        return this.injector.config.getAppKey();
    }

    public String getAppSecret ()
    {
        return this.injector.config.getAppSecret();
    }

    private void initSession ()
    {
        this.injector.setSession(new Session());
        OAuthIO oauthIO = new OAuthIO();
        oauthIO.setTokens(new LinkedList<String>());
        this.injector.getSession().setOauthIO(oauthIO);

    }

    public String generateStateToken ()
    {
        String uniqueToken = OAuthUtils.encryptKey(UUID.randomUUID().toString());

        this.injector.getSession().getOauthIO().getTokens().add(uniqueToken);

        if (this.injector.getSession().getOauthIO().getTokens().size() > 4)
        {
            List<String> subList = this.injector.getSession().getOauthIO().getTokens().subList(0, 4);
            this.injector.getSession().getOauthIO().setTokens(subList);

        }

        return uniqueToken;
    }

    public String redirect (String provider, String url)
    {
        String location = "";
        try
        {
            String urlToRedirect = properties.getProtocol() + "://" + properties.getHost() + (!properties.getPort().isEmpty() ? ":" + properties.getPort() : "") + url;
            String csrf = this.generateStateToken();

            JSONObject state = new JSONObject();
            state.put("state", csrf);

            urlToRedirect = URLEncoder.encode(urlToRedirect, "UTF-8");
            String opts = URLEncoder.encode(state.toString(), "UTF-8");
            location = this.injector.getConfig().getOauthUrl() +
                    this.injector.getConfig().getOauthdBase()
                    + "/" + provider
                    + "?k=" + this.injector.getConfig().getAppKey()
                    + "&opts=" + opts
                    + "&redirect_type=server&redirect_uri=" + urlToRedirect;
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();

            return null;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }


        return location;
    }

    public JSONObject refreshCredentials (JSONObject credentials, boolean force)
    {
        try
        {
            Date date = new Date();

            credentials.put("refreshed", false);

            if ((credentials.has("refresh_token") && (credentials.has("expires") && date.getTime() > ((Date) credentials.get("expires")).getTime())) || force)
            {
                HttpWrapper request = this.injector.getRequest();

                String url = this.injector.getConfig().getOauthUrl() + this.injector.getConfig().getOauthdBase() + "/refresh_token/" + credentials.get("provider");

                Map<String, Object> fields = new HashMap<String, Object>();
                fields.put("token", credentials.get("refresh_token").toString());
                fields.put("key", this.injector.getConfig().getAppKey());
                fields.put("secret", this.injector.getConfig().getAppSecret());

                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                headers.put("accept", "application/json");


                JsonNode response = request.makeRequest(url, HttpVerbs.POST, headers, null, fields, null).getBody();

                JSONObject refreshedCreds = response.getObject();
                credentials = refreshedCreds;
                credentials.put("refreshed", true);

            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return credentials;
    }

    public RequestObject auth (String provider) throws NotAuthenticatedException, NotInitializedException
    {
        return auth(provider, null);
    }

    public RequestObject auth (String provider, OauthOptions options) throws NotInitializedException, NotAuthenticatedException
    {
        //JSONObject data;
        JSONObject credentials = null;
        String code = "";

        if (!this.initialized)
        {
            throw new NotInitializedException();
        }
        else
        {
            if (options != null)
            {
/*               if (options.get("redirect") != null && (Boolean)options.get("redirect"))
                {
                    data = oauthio.getJSONObject("data");
                    code = data.getString("code");
                }*/
                if (options.getCode() != null)
                {
                    code = options.getCode().toString();
                }

                if (code != null && !code.isEmpty())
                {
                    HttpWrapper request = this.injector.getRequest();

                    String url = this.injector.getConfig().getOauthUrl() + this.injector.getConfig().getOauthdBase() + "/access_token";

                    Map<String, Object> fields = new HashMap<String, Object>();

                    fields.put("code", code);
                    fields.put("key", this.injector.getConfig().getAppKey());
                    fields.put("secret", this.injector.getConfig().getAppSecret());

                    Map<String, String> headers = new HashMap<String, String>();

                    /*headers.put("Content-Type", "application/x-www-form-urlencoded");
                    headers.put("accept", "application/json");*/


                    try
                    {
                        HttpResponse<JsonNode> result = request.makeRequest(url, HttpVerbs.POST, headers, null, fields, null);

                        JsonNode creds = result.getBody();

                        credentials = creds.getObject();

                        if (credentials.has("expires_in"))
                        {
                            Date date = new Date();
                            Long expires = date.getTime() + credentials.getLong("expires_in");
                            credentials.put("expires", expires);
                        }

                        if (credentials.has("provider"))
                        {
                            String prov = credentials.get("provider").toString();
                            this.injector.getSession().getOauthIO().getAuth().put(prov, credentials);
                        }
                    }

                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                }
                else if (options.getCredentials() != null)
                {
                    credentials = new JSONObject( options.getCredentials() );
                }
            }
            else
            {
                if (this.injector.getSession().getOauthIO().getAuth().get(provider) != null)
                {
                    credentials = new JSONObject(this.injector.getSession().getOauthIO().getAuth().get(provider));
                }
                else
                {
                    throw new NotAuthenticatedException();
                }
            }

            Boolean forceRefresh = options.isForceRefresh() != null ?  options.isForceRefresh() : false;

            credentials = this.refreshCredentials(credentials, forceRefresh);

            RequestObject requestObject = new RequestObject(credentials);

            return requestObject;
        }
    }


}
