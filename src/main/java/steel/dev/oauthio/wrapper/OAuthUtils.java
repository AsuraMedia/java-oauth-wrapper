package steel.dev.oauthio.wrapper;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import steel.dev.oauthio.wrapper.http.QueryStringBuilder;

/**
 * Created by eduardo on 23/01/15.
 */
public class OAuthUtils
{
  public static String encryptKey(final String password)
  {
    String sha1 = "";
    try
    {
      final MessageDigest crypt = MessageDigest.getInstance("SHA-1");
      crypt.reset();
      crypt.update(password.getBytes("UTF-8"));
      sha1 = byteToHex(crypt.digest());
    }
    catch (final NoSuchAlgorithmException e)
    {
      e.printStackTrace();
    }
    catch (final UnsupportedEncodingException e)
    {
      e.printStackTrace();
    }
    return sha1;
  }

  public static String byteToHex(final byte[] hash)
  {
    final Formatter formatter = new Formatter();
    for (final byte b : hash)
    {
      formatter.format("%02x", b);
    }
    final String result = formatter.toString();
    formatter.close();
    return result;
  }

  public static Map jsonToMap(final JSONObject json) throws JSONException
  {
    Map<String, Object> retMap = new HashMap<String, Object>();

    if (json != JSONObject.NULL)
    {
      retMap = toMap(json);
    }
    return retMap;
  }

  public static Map toMap(final JSONObject object) throws JSONException
  {
    final Map<String, Object> map = new HashMap<String, Object>();

    final Iterator<String> keysItr = object.keys();
    while (keysItr.hasNext())
    {
      final String key = keysItr.next();
      Object value = object.get(key);

      if (value instanceof JSONArray)
      {
        value = toList((JSONArray) value);
      }

      else if (value instanceof JSONObject)
      {
        value = toMap((JSONObject) value);
      }
      map.put(key, value);
    }
    return map;
  }

  public static List toList(final JSONArray array) throws JSONException
  {
    final List<Object> list = new ArrayList<Object>();
    for (int i = 0; i < array.length(); i++)
    {
      Object value = array.get(i);
      if (value instanceof JSONArray)
      {
        value = toList((JSONArray) value);
      }

      else if (value instanceof JSONObject)
      {
        value = toMap((JSONObject) value);
      }
      list.add(value);
    }
    return list;
  }

  public static String httpBuildQuery(final Map<String, Object> data)
      throws UnsupportedEncodingException
  {
    final QueryStringBuilder builder = new QueryStringBuilder();

    for (final Map.Entry<String, Object> pair : data.entrySet())
    {
      builder.addQueryParameter(pair.getKey(), String.valueOf(pair.getValue()));
    }
    return builder.encode("UTF-8");
  }
}
