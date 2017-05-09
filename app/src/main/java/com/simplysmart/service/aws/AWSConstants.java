package com.simplysmart.service.aws;

import com.amazonaws.regions.Regions;

public class AWSConstants {

    //URLs for create identity & bucket
    //https://console.aws.amazon.com/cognito/home?region=us-east-1 (Manage federated identities)
    //https://console.aws.amazon.com/iam/home?region=us-east-1#home

    //Xrbia AWS S3 bucket details
    public static final String COGNITO_IDENTITY_ID = "us-east-1:4feebf1c-8998-481e-a880-beb363d28118";
    public static final Regions COGNITO_REGION = Regions.US_EAST_1;
    public static final String BUCKET_NAME = "xrbia-township";
    public static final int SOCKET_TIMEOUT = 60000;
    public static final int RETRIES = 3;

    public static final String S3_URL = "https://s3.amazonaws.com/";
    public static final String PATH_FOLDER = "mobile/";
}
