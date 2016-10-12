package steel.dev.oauthio.wrapper;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by eduardo on 23/01/15.
 */
public class OAuthIO
{
  public List<String> tokens = new LinkedList<String>();
  public Map<String, Object> auth = new HashMap<String, Object>();

  public List<String> getTokens()
  {
    return this.tokens;
  }

  public void setTokens(final List<String> tokens)
  {
    this.tokens = tokens;
  }

  public Map<String, Object> getAuth()
  {
    return this.auth;
  }

  public void setAuth(final Map<String, Object> auth)
  {
    this.auth = auth;
  }
}
