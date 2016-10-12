package steel.dev.oauthio.wrapper.config;

/**
 * Created by eduardo on 14/04/15.
 */
public class OauthOptions
{
  String code;
  String credentials;
  Boolean forceRefresh;

  public String getCode()
  {
    return this.code;
  }

  public void setCode(final String code)
  {
    this.code = code;
  }

  public String getCredentials()
  {
    return this.credentials;
  }

  public void setCredentials(final String credentials)
  {
    this.credentials = credentials;
  }

  public Boolean isForceRefresh()
  {
    return this.forceRefresh;
  }

  public void setForceRefresh(final Boolean forceRefresh)
  {
    this.forceRefresh = forceRefresh;
  }
}
