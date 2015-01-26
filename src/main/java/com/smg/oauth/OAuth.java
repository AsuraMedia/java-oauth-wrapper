package com.smg.oauth;


import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;
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
    public Properties properties = new Properties();
    public Callback<JsonNode> refreshCallback = new Callback<JsonNode>()
    {
        @Override
        public void completed (HttpResponse<JsonNode> httpResponse)
        {
            System.out.println("Credentials refreshed");
        }

        @Override
        public void failed (UnirestException e)
        {
            System.out.println("Failing refreshing credentials");
        }

        @Override
        public void cancelled ()
        {
            System.out.println("Cancelled refreshed credentials");
        }
    };


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

    public void setSssion (Map<String, Object> session)
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
        if (base.equalsIgnoreCase("/"))
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
        this.injector.session = new HashMap<String, Object>();
        OauthIO oauthIO = new OauthIO();
        oauthIO.tokens = new LinkedList<String>();
        this.injector.session.put("oauthio", oauthIO);

    }

    public String generateStateToken ()
    {
        String uniqueToken = OAuthUtils.encryptKey(UUID.randomUUID().toString());

        ((OauthIO) this.injector.session.get("oauthio")).tokens.push(uniqueToken);

        if (((OauthIO) this.injector.session.get("oauthio")).tokens.size() > 4)
        {
            LinkedList<String> subList = (LinkedList<String>) ((OauthIO) this.injector.session.get("oauthio")).tokens.subList(0, 4);

            ((OauthIO) this.injector.session.get("oauthio")).tokens = subList;

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

            String opts = "";

            urlToRedirect = URLEncoder.encode(urlToRedirect, "UTF-8");
            opts = URLEncoder.encode(state.toString(), "UTF-8");
            location = this.injector.config.getOauthUrl() +
                    this.injector.config.getOauthdBase()
                    + "/" + provider
                    + "?k=" + this.injector.config.getAppKey()
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

                String url = this.injector.config.getOauthUrl() + this.injector.config.getOauthdBase() + "/refresh_token/" + credentials.get("provider");

                Map<String, Object> fields = new HashMap<String, Object>();
                fields.put("token", credentials.get("refresh_token").toString());
                fields.put("key", this.injector.config.getAppKey());
                fields.put("secret", this.injector.config.getAppSecret());

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

    public RequestObject auth (String provider, Map<String, Object> options) throws NotInitializedException, NotAuthenticatedException
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
                if (options.get("code") != null)
                {
                    code = options.get("code").toString();
                }

                if (code != null && !code.isEmpty())
                {
                    HttpWrapper request = this.injector.getRequest();

                    String url = this.injector.config.getOauthUrl() + this.injector.config.getOauthdBase() + "/access_token";

                    Map<String, Object> fields = new HashMap<String, Object>();

                    fields.put("code", code);
                    fields.put("key", this.injector.config.getAppKey());
                    fields.put("secret", this.injector.config.getAppSecret());

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
                            ((OauthIO) this.injector.session.get("oauthio")).auth.put(credentials.get("provider").toString(), credentials);
                        }
                    }

                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                }
                else if (options.get("credentials") != null)
                {
                    credentials = (JSONObject) options.get("credentials");
                }
            }
            else
            {
                if (((OauthIO) this.injector.session.get("oauthio")).auth.get(provider) != null)
                {
                    credentials = (JSONObject) ((OauthIO) this.injector.session.get("oauthio")).auth.get(provider);
                }
                else
                {
                    throw new NotAuthenticatedException();
                }
            }
            Boolean forceRefresh = options.get("force_refresh") != null ? (Boolean) options.get("force_refresh") : false;

            credentials = this.refreshCredentials(credentials, forceRefresh);

            RequestObject requestObject = new RequestObject(credentials);

            return requestObject;
        }
    }


}
