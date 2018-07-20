package com.simplysmart.service.endpint;


import com.google.gson.JsonObject;
import com.simplysmart.service.model.attendance.AttendanceAt;
import com.simplysmart.service.model.attendance.AttendanceList;
import com.simplysmart.service.model.category.CategoryResponse;
import com.simplysmart.service.model.common.CommonResponse;
import com.simplysmart.service.model.helpdesk.ComplaintChatRequest;
import com.simplysmart.service.model.helpdesk.ComplaintChatResponse;
import com.simplysmart.service.model.helpdesk.ComplaintDetailResponse;
import com.simplysmart.service.model.helpdesk.ComplaintRequest;
import com.simplysmart.service.model.helpdesk.ComplaintUpdateRequest;
import com.simplysmart.service.model.helpdesk.HelpDeskResponse;
import com.simplysmart.service.model.helpdesk.MessageResponseClass;
import com.simplysmart.service.model.matrix.AllReadingsData;
import com.simplysmart.service.model.matrix.MatrixReadingData;
import com.simplysmart.service.model.matrix.MatrixResponse;
import com.simplysmart.service.model.matrix.ReadingData;
import com.simplysmart.service.model.sensor.SensorList;
import com.simplysmart.service.model.sensor.SensorReadingGraphResponse;
import com.simplysmart.service.model.user.LoginRequest;
import com.simplysmart.service.model.user.LoginResponse;
import com.simplysmart.service.model.visitors.VisitorPost;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by shekhar on 13/05/16.
 */
public interface ApiInterface {

    //User login
    @POST("/api/sessions/sign_in")
    Call<LoginResponse> residentLogin(@Body LoginRequest request);

    @DELETE("/api/sessions")
    void logOut(Callback<CommonResponse> callback);

    //Resident logout
    @DELETE("/api/sessions/sign_out")
    Call<CommonResponse> residentLogout(@Query("subdomain") String subDomain);

    @POST("/api/sessions/sign_in")
    Call<LoginResponse> residentLoginWithSubDomain(@Query("subdomain") String subDomain, @Body LoginRequest request);

    @GET("/api/metrics")
    Call<MatrixResponse> getMetrics(@Query("site_id") String siteId,
                                    @Query("subdomain") String subDomain);

    @POST("/api/sensor_readings/")
    Call<JsonObject> submitReading(@Query("subdomain") String subDomain,
                                   @Body ReadingData readingData);

    @POST("/api/metrics")
    Call<JsonObject> submitAllReadings(@Query("subdomain") String subDomain,
                                       @Body AllReadingsData allReadingsData);

    @GET("/api/attendances")
    Call<AttendanceAt> getAttendanceTime(@Query("subdomain") String subdomain);

    @POST("/api/attendances")
    Call<JsonObject> sendAttendances(
            @Query("subdomain") String subdomain,
            @Body AttendanceList list);

    @POST("/api/visitors")
    Call<JsonObject> sendVisitors(@Query("subdomain") String subdomain,
                                  @Body VisitorPost post);


    //Fetch categories
    @GET("/cms/categories")
    Call<CategoryResponse> fetchCategories(@Query("subdomain") String subDomain);

    @GET("/cms/complaints")
    Call<HelpDeskResponse> getComplaintsData(@Query("subdomain") String subDomain,
                                             @Query("state") String state,
                                             @Query("page") String pageNumber);

    @GET("/cms/complaints/{complaintId}")
    Call<ComplaintDetailResponse> getComplaintDetails(@Path("complaintId") String complaintId,
                                                      @Query("subdomain") String subDomain);

    @POST("/cms/complaints/{complaintId}/activity")
    Call<ComplaintChatResponse> postComment(@Path("complaintId") String complaintId,
                                            @Query("subdomain") String subDomain,
                                            @Body ComplaintChatRequest request);

    @PUT("/cms/complaints/{complaintId}")
    Call<MessageResponseClass> updateComplaintStatus(@Path("complaintId") String complaintId,
                                                     @Query("subdomain") String subDomain,
                                                     @Body ComplaintUpdateRequest complaint);


    @POST("/cms/complaints")
    Call<CommonResponse> createNewTicket(@Query("subdomain") String subDomain,
                                         @Body ComplaintRequest request);

    // get readings for all sensors
    @GET("api/admin/sensors")
    Call<SensorList> getSensorsReadings(@Query("subdomain") String subDomain,
                                        @Query("site_id") String state,
                                        @Query("type") String type);

    // get consumption readings for all sensors
    @GET("api/sensor_readings")
    Call<SensorReadingGraphResponse> getReadingsGraph(@Query("subdomain") String subDomain,
                                                      @Query("sensor_key") String sensor_key,
                                                      @Query("from_date") String from_date,
                                                      @Query("to_date") String to_date,
                                                      @Query("type") String type);


    @POST("/api/metrics/")
    Call<JsonObject> submitMatrixReading(@Body MatrixReadingData readingData);
}
