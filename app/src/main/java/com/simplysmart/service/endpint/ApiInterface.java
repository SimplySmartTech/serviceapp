package com.simplysmart.service.endpint;


import com.google.gson.JsonObject;
import com.simplysmart.service.model.matrix.AllReadingsData;
import com.simplysmart.service.model.matrix.MatrixResponse;
import com.simplysmart.service.model.matrix.ReadingData;
import com.simplysmart.service.model.user.LoginRequest;
import com.simplysmart.service.model.user.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by shekhar on 13/05/16.
 */
public interface ApiInterface {

    //User login
    @POST("/api/sessions/sign_in")
    Call<LoginResponse> residentLogin(@Body LoginRequest request);

    @POST("/api/sessions/sign_in")
    Call<LoginResponse> residentLoginWithSubDomain(@Query("subdomain") String subDomain, @Body LoginRequest request);

    @GET("/api/metrics")
    Call<MatrixResponse> getMetrics(@Query("unit_id") String unitId,
                                    @Query("subdomain") String subDomain);

    @POST("/api/sensor_readings/")
    Call<JsonObject> submitReading(@Query("subdomain") String subDomain,
                                   @Body ReadingData readingData);

    @POST("/api/sensor_readings/")
    Call<JsonObject> submitAllReadings(@Query("subdomain") String subDomain,
                                       @Body AllReadingsData allReadingsData);

}
