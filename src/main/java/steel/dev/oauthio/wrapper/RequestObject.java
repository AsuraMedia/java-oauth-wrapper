package steel.dev.oauthio.wrapper;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.exceptions.UnirestException;

import steel.dev.oauthio.wrapper.constants.HttpVerbs;
import steel.dev.oauthio.wrapper.http.HttpWrapper;

/**
 * Created by eduardo on 23/01/15.
 */
public class RequestObject
{
  private final Injector injector;
  private final JSONObject credentials;

  public RequestObject(final JSONObject credentials)
  {
    this.injector = Injector.getInstance();
    this.credentials = credentials;
  }

  public JSONObject getCredentials()
  {
    return this.credentials;
  }

  public boolean wasRefreshed()
  {
    this.credentials.put("refreshed", true);
    return (Boolean) this.credentials.get("refreshed");
  }

  private <T> JsonNode objectToJson(final Class<T> type, final T object)
  {
    return new JsonNode("");
  }

  private HttpResponse<JsonNode> makeRequest(final HttpVerbs method, final String url,
      final JSONObject body, final Map<String, Object> queryParams) throws UnirestException
  {

    HttpResponse<JsonNode> response = null;
    String fullUrl = "";
    final JSONObject provData = this.credentials;
    final HttpWrapper requester = this.injector.getRequest();
    final Map<String, String> headers = new HashMap<String, String>();
    final Map<String, Object> params = new HashMap<String, Object>();

    params.put("k", this.injector.config.getAppKey());

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
      fullUrl = this.injector.config.getOauthUrl() + "/request/"
          + this.credentials.get("provider").toString() + "/" + URLEncoder.encode(url, "UTF-8");
    }
    catch (final UnsupportedEncodingException e)
    {
      return null;
    }

    response = requester.makeRequest(fullUrl, method, headers,
        body != null ? body.toString() : null, null, queryParams);

    return response;
  }

  private HttpResponse<JsonNode> makeRequest(final String... filters) throws UnirestException
  {
    HttpResponse<JsonNode> response = null;
    String fullUrl = "";
    final JSONObject provData = this.credentials;
    final HttpWrapper requester = this.injector.getRequest();

    final Map<String, Object> params = new HashMap<String, Object>();
    final Map<String, String> filterMap = new HashMap<String, String>();
    final Map<String, String> headers = new HashMap<String, String>();

    params.put("k", this.injector.config.getAppKey());

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
      fullUrl =
          this.injector.config.getOauthUrl() + "/auth/" + this.credentials.get("provider") + "/me";
      headers.put("oauthio", OAuthUtils.httpBuildQuery(params));
    }

    catch (final UnsupportedEncodingException e)
    {
      return null;
    }

    final Map<String, Object> filterQuery = new HashMap<String, Object>();

    filterQuery.put("filter", StringUtils.join(filters, ","));

    response = requester.makeRequest(fullUrl, HttpVerbs.GET, headers, null, null, filterQuery);

    return response;
  }

  public HttpResponse<JsonNode> get(final String url)
  {
    return get(url, null);
  }

  public HttpResponse<JsonNode> get(final String url, final Map<String, Object> queryParams)
  {
    HttpResponse<JsonNode> response = null;
    try
    {
      response = this.makeRequest(HttpVerbs.GET, url, null, queryParams);
    }
    catch (final UnirestException e)
    {
      e.printStackTrace();
    }

    return response;
  }

  public HttpResponse<JsonNode> post(final String url, final String json)
  {
    final JSONObject body = new JSONObject(json);

    HttpResponse<JsonNode> response = null;
    try
    {
      response = this.makeRequest(HttpVerbs.POST, url, body, null);
    }
    catch (final UnirestException e)
    {
      e.printStackTrace();
    }
    return response;
  }

  public HttpResponse<JsonNode> put(final String url, final String json)
  {
    final JSONObject body = new JSONObject(json);
    HttpResponse<JsonNode> response = null;
    try
    {
      response = this.makeRequest(HttpVerbs.PUT, url, body, null);
    }
    catch (final UnirestException e)
    {
      e.printStackTrace();
    }

    return response;
  }

  public HttpResponse<JsonNode> patch(final String url, final String json)
  {
    final JSONObject body = new JSONObject(json);
    HttpResponse<JsonNode> response = null;
    try
    {
      response = this.makeRequest(HttpVerbs.PATCH, url, body, null);
    }
    catch (final UnirestException e)
    {
      e.printStackTrace();
    }

    return response;
  }

  public HttpResponse<JsonNode> delete(final String url)
  {
    HttpResponse<JsonNode> response = null;
    try
    {
      response = this.makeRequest(HttpVerbs.DELETE, url, null, null);
    }
    catch (final UnirestException e)
    {
      e.printStackTrace();
    }

    return response;
  }

  public HttpResponse<JsonNode> me(final String... filters)
  {
    HttpResponse<JsonNode> response = null;
    try
    {
      response = this.makeRequest(filters);
    }
    catch (final UnirestException e)
    {
      e.printStackTrace();
    }

    return response;
  }

}
