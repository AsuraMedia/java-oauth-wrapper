package steel.dev.oauthio.wrapper.constants;

/**
 * Created by eduardo on 23/01/15.
 */
public enum HttpVerbs
{
  GET("GET"), POST("POST"), PUT("PUT"), DELETE("DELETE"), PATCH("PATCH");

  String value;

  HttpVerbs(final String value)
  {
    this.value = value;
  }

}
