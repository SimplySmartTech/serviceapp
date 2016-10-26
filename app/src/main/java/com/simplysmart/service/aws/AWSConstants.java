package com.simplysmart.service.aws;

import com.amazonaws.regions.Regions;

public class AWSConstants {

    //URLs for create identity & bucket
    //https://console.aws.amazon.com/cognito/home?region=us-east-1 (Manage federated identities)
    //https://console.aws.amazon.com/iam/home?region=us-east-1#home

    public static final String COGNITO_IDENTITY_ID = "us-east-1:77a46069-c8b7-4864-8cb8-c008d853d77e";
    public static final Regions COGNITO_REGION = Regions.US_EAST_1;
    public static final String BUCKET_NAME = "simplysmart";
    public static final int SOCKET_TIMEOUT = 60000;

    public static final String S3_URL = "https://s3.amazonaws.com/";
    public static final String PATH_FOLDER = "service/";
}
