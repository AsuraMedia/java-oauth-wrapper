package com.smg.oauth;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * Created by eduardo on 23/01/15.
 */
public class RequestObject
{
    private Injector injector;
    private JSONObject credentials;

    public RequestObject(JSONObject  credentials)
    {
        this.injector = Injector.getInstance();
        this.credentials = credentials;
    }

    public JSONObject  getCredentials()
    {
        return this.credentials;
    }

    public boolean wasRefreshed()
    {
        credentials.put("refreshed",true);
        return (Boolean)credentials.get("refreshed");
    }

    private <T> JsonNode objectToJson(Class<T> type, T object)
    {
        return new JsonNode("");
    }

    private HttpResponse<JsonNode> makeRequest(HttpVerbs method, String url, JSONObject body) throws UnirestException
    {


        HttpResponse<JsonNode> response = null;
        String fullUrl="";
        JSONObject  provData = this.credentials;
        HttpWrapper requester = this.injector.getRequest();
        Map<String, String> headers = new HashMap<String, String>();
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("k", injector.config.getAppKey());

        if (provData.has("access_token"))
        {
            params.put("access_token", provData.get("access_token").toString());
        }

        if (provData.has("oauth_token") && provData.has("oauth_token_secret"))
        {
            params.put("oauth_token", provData.get("oauth_token").toString());
            params.put("oauth_token_secret", provData.get("oauth_token_secret").toString());
            params.put("oauthv1", "1");
        }

        try
        {
            headers.put("oauthio", OAuthUtils.httpBuildQuery(params));
            fullUrl = this.injector.config.getOauthUrl()+"/request/"+this.credentials.get("provider").toString()+"/"+ URLEncoder.encode(url,"UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            return null;
        }

        response = requester.makeRequest(fullUrl, method, headers, body!=null?body.toString():null,null,null);

        return response;
    }


    private HttpResponse<JsonNode> makeRequest( String... filters) throws UnirestException
    {
        HttpResponse<JsonNode> response = null;
        String fullUrl = "";
        JSONObject  provData = this.credentials;
        HttpWrapper requester = this.injector.getRequest();

        Map<String, Object> params = new HashMap<String, Object>();
        Map<String, String> filterMap = new HashMap<String, String>();
        Map<String, String> headers = new HashMap<String, String>();

        params.put("k", injector.config.getAppKey());

        if (provData.has("access_token"))
        {
            params.put("access_token", provData.get("access_token").toString());
        }

        if (provData.has("oauth_token") && provData.has("oauth_token_secret"))
        {
            params.put("oauth_token", provData.get("oauth_token").toString());
            params.put("oauth_token_secret", provData.get("oauth_token_secret").toString());
            params.put("oauthv1", "1");
        }

        try
        {
            fullUrl = this.injector.config.oauthUrl+"/auth/"+this.credentials.get("provider")+"/me";
            headers.put("oauthio", OAuthUtils.httpBuildQuery(params));
        }

        catch (UnsupportedEncodingException e)
        {
            return null;
        }

        Map<String,Object> filterQuery = new HashMap<String, Object>();

        filterQuery.put("filter", StringUtils.join(filters, ","));

        response = requester.makeRequest(fullUrl, HttpVerbs.GET, headers,null,null, filterQuery);

        return response;
    }

    public HttpResponse<JsonNode> get(String url)
    {
        HttpResponse<JsonNode> response = null;
        try
        {
            response = this.makeRequest(HttpVerbs.GET,url,null);
        }
        catch (UnirestException e)
        {
            e.printStackTrace();
        }

        return response;
    }

    public HttpResponse<JsonNode> post(String url, String json)
    {
        JSONObject body  = new JSONObject(json);

        HttpResponse<JsonNode> response = null;
        try
        {
            response = this.makeRequest(HttpVerbs.POST,url,body);
        }
        catch (UnirestException e)
        {
            e.printStackTrace();
        }
        return response;
    }

    public HttpResponse<JsonNode> put(String url, String json)
    {
        JSONObject body  = new JSONObject(json);
        HttpResponse<JsonNode> response = null;
        try
        {
            response = this.makeRequest(HttpVerbs.PUT,url,body);
        }
        catch (UnirestException e)
        {
            e.printStackTrace();
        }

        return response;
    }

    public HttpResponse<JsonNode> patch(String url, String json)
    {
        JSONObject body  = new JSONObject(json);
        HttpResponse<JsonNode> response = null;
        try
        {
            response = this.makeRequest(HttpVerbs.PATCH,url,body);
        }
        catch (UnirestException e)
        {
            e.printStackTrace();
        }

        return response;
    }

    public HttpResponse<JsonNode> delete(String url)
    {
        HttpResponse<JsonNode> response = null;
        try
        {
            response = this.makeRequest(HttpVerbs.DELETE,url,null);
        }
        catch (UnirestException e)
        {
            e.printStackTrace();
        }

        return response;
    }

    public HttpResponse<JsonNode> me( String... filters)
    {
        HttpResponse<JsonNode> response = null;
        try
        {
            response = this.makeRequest(filters);
        }
        catch (UnirestException e)
        {
            e.printStackTrace();
        }

        return response;
    }


}
