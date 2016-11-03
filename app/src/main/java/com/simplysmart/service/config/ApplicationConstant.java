package com.simplysmart.service.config;

public class ApplicationConstant {

    //Flag for debugging log mode
    public static boolean isDebuggable = false;

    //Api url component
    public static final String PROTOCOL = "http://";
    public static final String DOMAIN = "api.simplysmart.tech";//"192.168.1.59:3000";

    public static final String SERVER_URL = PROTOCOL + DOMAIN;

    // Http request header content
    public static final String ACCEPT_TYPE = "application/vnd.botsworth.v1+json";
    public static final String CONTENT_TYPE = "application/json";
    public static final String CONTENT_LOCALE = "en-US";

    //Cloudinary URL
    public static final String cloudinary_base_path = "https://res.cloudinary.com/mixtape/image/upload/";

}
