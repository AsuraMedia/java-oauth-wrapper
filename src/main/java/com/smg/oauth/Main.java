package com.smg.oauth;

/**
 * Created by eduardo on 23/01/15.
 */
public class Main
{
    public static void main(String[] Args) throws NotAuthenticatedException, NotInitializedException
    {
        OAuth oAuth = new OAuth();

        oAuth.initialize("2F8e-NhDjrhc-lzk0kpei16mM1c","oQEPiSdX3ToOWutEvkbwT9P5Ddk");

        String url = oAuth.redirect("twitter","/");
        System.out.println(url);








    }
}
