package com.simplysmart.service.config;

public class ApplicationConstant {

    //Flag for debugging log mode
    public static boolean isDebuggable = true;

    //Api url component
    public static final String PROTOCOL = "https://";
//    public static final String DOMAIN = "192.168.1.86:3000";
    public static final String DOMAIN ="api.simplysmart.tech";

    public static final String SERVER_URL = PROTOCOL + DOMAIN;

    // Http request header content
    public static final String ACCEPT_TYPE = "application/vnd.simplysmart.v1+json";
    public static final String ACCEPT_TYPE_V2 = "application/vnd.simplysmart.v2+json";
    public static final String CONTENT_TYPE = "application/json";
    public static final String CONTENT_LOCALE = "en-US";

}
