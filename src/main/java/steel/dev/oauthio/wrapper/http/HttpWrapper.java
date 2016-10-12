package steel.dev.oauthio.wrapper.http;

import java.util.HashMap;
import java.util.Map;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import steel.dev.oauthio.wrapper.constants.HttpVerbs;

/**
 * Created by eduardo on 23/01/15.
 */
public class HttpWrapper
{

  Map<String, String> headers = new HashMap<String, String>();

  public HttpWrapper()
  {

  }

  public HttpResponse<JsonNode> makeRequest(final String url, final HttpVerbs method,
      final Map<String, String> headers, final String body, final Map<String, Object> fields,
      final Map<String, Object> filters) throws UnirestException
  {
    HttpResponse<JsonNode> response = null;

    if (method == HttpVerbs.GET)
    {
      if (filters != null)
      {
        response = Unirest.get(url).headers(headers).queryString(filters).asJson();
      }
      else
      {
        response = Unirest.get(url).headers(headers).asJson();
      }
    }

    else if (method == HttpVerbs.POST)
    {

      if (body != null)
      {
        response = Unirest.post(url).headers(headers).body(body).asJson();
      }
      else if (filters != null)
      {
        response = Unirest.post(url).headers(headers).queryString(filters).fields(fields).asJson();
      }
      else
      {
        response = Unirest.post(url).headers(headers).fields(fields).asJson();
      }
    }

    else if (method == HttpVerbs.PUT)
    {
      response = Unirest.put(url).headers(headers).body(body).asJson();
    }

    else if (method == HttpVerbs.DELETE)
    {
      response = Unirest.delete(url).headers(headers).asJson();
    }

    return response;
  }

}
