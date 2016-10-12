package steel.dev.oauthio.wrapper.config;

/**
 * Created by eduardo on 23/01/15.
 */
public class OauthConfig
{
  String oauthUrl = "http://localhost:6284";
  String oauthdBase = "/auth";
  String appKey = "";
  String appSecret = "";

  public OauthConfig(final String oautdhUrl, final String oauthdBase, final String appKey,
      final String appSecret)
  {
    this.oauthUrl = oautdhUrl;
    this.oauthdBase = oauthdBase;
    this.appKey = appKey;
    this.appSecret = appSecret;
  }

  public OauthConfig()
  {
  }

  public OauthConfig(final String appKey, final String appSecret)
  {
    this.appKey = appKey;
    this.appSecret = appSecret;
  }

  public String getOauthUrl()
  {
    return this.oauthUrl;
  }

  public void setOauthUrl(final String oauthUrl)
  {
    this.oauthUrl = oauthUrl;
  }

  public String getOauthdBase()
  {
    return this.oauthdBase;
  }

  public void setOauthdBase(final String oauthdBase)
  {
    this.oauthdBase = oauthdBase;
  }

  public String getAppKey()
  {
    return this.appKey;
  }

  public void setAppKey(final String appKey)
  {
    this.appKey = appKey;
  }

  public String getAppSecret()
  {
    return this.appSecret;
  }

  public void setAppSecret(final String appSecret)
  {
    this.appSecret = appSecret;
  }
}
