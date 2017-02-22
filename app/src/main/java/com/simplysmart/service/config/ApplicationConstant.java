package com.simplysmart.service.config;

public class ApplicationConstant {

    //Flag for debugging log mode
    public static boolean isDebuggable = false;

    //Api url component
    public static final String PROTOCOL = "http://";
    public static final String DOMAIN ="192.168.1.25:3000";// "api.simplysmart.tech";
//    public static final String DOMAIN ="api.simplysmart.tech";

    public static final String SERVER_URL = PROTOCOL + DOMAIN;

    // Http request header content
    public static final String ACCEPT_TYPE = "application/vnd.botsworth.v1+json";
    public static final String CONTENT_TYPE = "application/json";
    public static final String CONTENT_LOCALE = "en-US";

}
