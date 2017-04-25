package com.simplysmart.service.endpint;


import com.google.gson.JsonObject;
import com.simplysmart.service.model.attendance.AttendanceAt;
import com.simplysmart.service.model.attendance.AttendanceList;
import com.simplysmart.service.model.helpdesk.ComplaintChatRequest;
import com.simplysmart.service.model.helpdesk.ComplaintChatResponse;
import com.simplysmart.service.model.helpdesk.ComplaintDetailResponse;
import com.simplysmart.service.model.helpdesk.ComplaintUpdateRequest;
import com.simplysmart.service.model.helpdesk.HelpDeskResponse;
import com.simplysmart.service.model.helpdesk.MessageResponseClass;
import com.simplysmart.service.model.matrix.AllReadingsData;
import com.simplysmart.service.model.matrix.MatrixResponse;
import com.simplysmart.service.model.matrix.ReadingData;
import com.simplysmart.service.model.user.LoginRequest;
import com.simplysmart.service.model.user.LoginResponse;
import com.simplysmart.service.model.visitors.VisitorPost;

import retrofit2.Call;
import retrofit2.http.Body;
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

    @POST("/api/sessions/sign_in")
    Call<LoginResponse> residentLoginWithSubDomain(@Query("subdomain") String subDomain, @Body LoginRequest request);

    @GET("/api/metrics")
    Call<MatrixResponse> getMetrics(@Query("unit_id") String unitId,
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

    @GET("/cms/complaints")
    Call<HelpDeskResponse> getComplaintsData(@Query("subdomain") String subDomain,
                                             @Query("aasm_state") String state,
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

}
