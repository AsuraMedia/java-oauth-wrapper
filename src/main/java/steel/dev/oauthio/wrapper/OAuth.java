package steel.dev.oauthio.wrapper;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

import org.json.JSONException;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;

import steel.dev.oauthio.wrapper.config.OauthOptions;
import steel.dev.oauthio.wrapper.config.Properties;
import steel.dev.oauthio.wrapper.constants.HttpVerbs;
import steel.dev.oauthio.wrapper.exceptions.NotAuthenticatedException;
import steel.dev.oauthio.wrapper.exceptions.NotInitializedException;
import steel.dev.oauthio.wrapper.http.HttpWrapper;

/**
 * Created by eduardo on 23/01/15.
 */
public class OAuth
{
  private final Injector injector;
  public Properties properties = new Properties();
  private boolean initialized = false;

  public OAuth()
  {
    this.injector = Injector.getInstance();
  }

  public void setSslVerification(final Boolean sslVerification)
  {
    this.injector.setSslVerification(sslVerification);
  }

  public void setSession(final Session session)
  {
    this.injector.session = session;
  }

  public String getOAuthUrl()
  {
    return this.injector.config.getOauthUrl();
  }

  public void setOAuthUrl(final String url, String base)
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

  public void initialize(final String key, final String secret)
  {
    this.injector.config.setAppKey(key);
    this.injector.config.setAppSecret(secret);
    this.initSession();
    this.initialized = true;

  }

  public String getAppKey()
  {
    return this.injector.config.getAppKey();
  }

  public String getAppSecret()
  {
    return this.injector.config.getAppSecret();
  }

  private void initSession()
  {
    this.injector.setSession(new Session());
    final OAuthIO oauthIO = new OAuthIO();
    oauthIO.setTokens(new LinkedList<String>());
    this.injector.getSession().setOauthIO(oauthIO);

  }

  public String generateStateToken()
  {
    final String uniqueToken = OAuthUtils.encryptKey(UUID.randomUUID().toString());

    this.injector.getSession().getOauthIO().getTokens().add(uniqueToken);

    if (this.injector.getSession().getOauthIO().getTokens().size() > 4)
    {
      final List<String> subList =
          this.injector.getSession().getOauthIO().getTokens().subList(0, 4);
      this.injector.getSession().getOauthIO().setTokens(subList);

    }

    return uniqueToken;
  }

  public String redirect(final String provider, final String url)
  {
    String location = "";
    try
    {
      String urlToRedirect = this.properties.getProtocol() + "://" + this.properties.getHost()
          + (!this.properties.getPort().isEmpty()
              ? ":" + this.properties.getPort()
              : "")
          + url;
      final String csrf = this.generateStateToken();

      final JSONObject state = new JSONObject();
      state.put("state", csrf);

      urlToRedirect = URLEncoder.encode(urlToRedirect, "UTF-8");
      final String opts = URLEncoder.encode(state.toString(), "UTF-8");
      location = this.injector.getConfig().getOauthUrl() +
          this.injector.getConfig().getOauthdBase() + "/" + provider + "?k="
          + this.injector.getConfig()
              .getAppKey()
          + "&opts=" + opts + "&redirect_type=server&redirect_uri=" + urlToRedirect;
    }
    catch (final UnsupportedEncodingException e)
    {
      e.printStackTrace();

      return null;
    }
    catch (final JSONException e)
    {
      e.printStackTrace();
    }

    return location;
  }

  public JSONObject refreshCredentials(JSONObject credentials, final boolean force)
  {
    try
    {
      final Date date = new Date();

      credentials.put("refreshed", false);

      if ((credentials.has("refresh_token")
          && (credentials.has("expires") && date.getTime() > ((Date) credentials.get("expires"))
              .getTime()))
          || force)
      {
        final HttpWrapper request = this.injector.getRequest();

        final String url = this.injector.getConfig().getOauthUrl()
            + this.injector.getConfig().getOauthdBase() + "/refresh_token/" + credentials
                .get("provider");

        final Map<String, Object> fields = new HashMap<String, Object>();
        fields.put("token", credentials.get("refresh_token").toString());
        fields.put("key", this.injector.getConfig().getAppKey());
        fields.put("secret", this.injector.getConfig().getAppSecret());

        final Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("accept", "application/json");

        final JsonNode response =
            request.makeRequest(url, HttpVerbs.POST, headers, null, fields, null).getBody();

        final JSONObject refreshedCreds = response.getObject();
        credentials = refreshedCreds;
        credentials.put("refreshed", true);

      }

    }
    catch (final Exception e)
    {
      e.printStackTrace();
    }
    return credentials;
  }

  public RequestObject auth(final String provider)
      throws NotAuthenticatedException, NotInitializedException
  {
    return auth(provider, null);
  }

  public RequestObject auth(final String provider, final OauthOptions options)
      throws NotInitializedException, NotAuthenticatedException
  {
    // JSONObject data;
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
        /*
         * if (options.get("redirect") != null && (Boolean)options.get("redirect")) { data =
         * oauthio.getJSONObject("data"); code = data.getString("code"); }
         */
        if (options.getCode() != null)
        {
          code = options.getCode().toString();
        }

        if (code != null && !code.isEmpty())
        {
          final HttpWrapper request = this.injector.getRequest();

          final String url = this.injector.getConfig().getOauthUrl()
              + this.injector.getConfig().getOauthdBase() + "/access_token";

          final Map<String, Object> fields = new HashMap<String, Object>();

          fields.put("code", code);
          fields.put("key", this.injector.getConfig().getAppKey());
          fields.put("secret", this.injector.getConfig().getAppSecret());

          final Map<String, String> headers = new HashMap<String, String>();

          /*
           * headers.put("Content-Type", "application/x-www-form-urlencoded"); headers.put("accept",
           * "application/json");
           */

          try
          {
            final HttpResponse<JsonNode> result =
                request.makeRequest(url, HttpVerbs.POST, headers, null, fields, null);

            final JsonNode creds = result.getBody();

            credentials = creds.getObject();

            if (credentials.has("expires_in"))
            {
              final Date date = new Date();
              final Long expires = date.getTime() + credentials.getLong("expires_in");
              credentials.put("expires", expires);
            }

            if (credentials.has("provider"))
            {
              final String prov = credentials.get("provider").toString();
              this.injector.getSession().getOauthIO().getAuth().put(prov, credentials);
            }
          }
          catch (final Exception e)
          {
            e.printStackTrace();
          }

        }
        else if (options.getCredentials() != null)
        {
          credentials = new JSONObject(options.getCredentials());
        }
      }
      else
      {
        if (this.injector.getSession().getOauthIO().getAuth().get(provider) != null)
        {
          credentials =
              new JSONObject(this.injector.getSession().getOauthIO().getAuth().get(provider));
        }
        else
        {
          throw new NotAuthenticatedException();
        }
      }

      final Boolean forceRefresh =
          options.isForceRefresh() != null ? options.isForceRefresh() : false;

      credentials = this.refreshCredentials(credentials, forceRefresh);

      final RequestObject requestObject = new RequestObject(credentials);

      return requestObject;
    }
  }

}
