package steel.dev.oauthio.wrapper;

import steel.dev.oauthio.wrapper.config.OauthConfig;
import steel.dev.oauthio.wrapper.http.HttpWrapper;

/**
 * Created by eduardo on 23/01/15.
 */
public class Injector
{
  private static Injector instance = null;
  public Session session = null;
  public OauthConfig config = new OauthConfig();
  public Boolean sslVerification;

  public static Injector getInstance()
  {
    if (instance == null)
    {
      instance = new Injector();
    }
    return instance;
  }

  public void setInstance(final Injector instance)
  {
    this.instance = instance;
  }

  public HttpWrapper getRequest()
  {
    return new HttpWrapper();
  }

  public Session getSession()
  {
    return this.session;
  }

  public void setSession(final Session session)
  {
    this.session = session;
  }

  public OauthConfig getConfig()
  {
    return this.config;
  }

  public void setConfig(final OauthConfig config)
  {
    this.config = config;
  }

  public Boolean getSslVerification()
  {
    return this.sslVerification;
  }

  public void setSslVerification(final Boolean sslVerification)
  {
    this.sslVerification = sslVerification;
  }
}
