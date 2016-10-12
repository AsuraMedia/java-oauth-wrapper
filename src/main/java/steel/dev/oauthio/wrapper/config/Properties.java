package steel.dev.oauthio.wrapper.config;

/**
 * Created by eduardo on 23/01/15.
 */
public class Properties
{
  public String protocol = "http";
  public String host = "localhost";
  public String port = "9000";

  public String getProtocol()
  {
    return this.protocol;
  }

  public void setProtocol(final String protocol)
  {
    this.protocol = protocol;
  }

  public String getHost()
  {
    return this.host;
  }

  public void setHost(final String host)
  {
    this.host = host;
  }

  public String getPort()
  {
    return this.port;
  }

  public void setPort(final String port)
  {
    this.port = port;
  }
}
