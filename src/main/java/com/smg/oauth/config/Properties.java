package com.smg.oauth.config;

/**
 * Created by eduardo on 23/01/15.
 */
public class Properties
{
    public  String protocol = "http";
    public  String host = "localhost";
    public  String port = "9000";

    public String getProtocol ()
    {
        return protocol;
    }

    public void setProtocol (String protocol)
    {
        this.protocol = protocol;
    }

    public String getHost ()
    {
        return host;
    }

    public void setHost (String host)
    {
        this.host = host;
    }

    public String getPort ()
    {
        return port;
    }

    public void setPort (String port)
    {
        this.port = port;
    }
}
