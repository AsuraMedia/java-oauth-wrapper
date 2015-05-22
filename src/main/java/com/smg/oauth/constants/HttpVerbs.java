package com.smg.oauth.constants;

/**
 * Created by eduardo on 23/01/15.
 */
public enum HttpVerbs
{
    GET ("GET"),
    POST ("POST"),
    PUT("PUT"),
    DELETE("DELETE"),
    PATCH("PATCH");

    String value;

    HttpVerbs(String value)
    {
        this.value = value;
    }

}
