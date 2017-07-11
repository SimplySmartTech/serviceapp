package com.simplysmart.service.request;

import com.simplysmart.service.callback.ApiCallback;
import com.simplysmart.service.config.ErrorUtils;
import com.simplysmart.service.config.GlobalData;
import com.simplysmart.service.config.ServiceGenerator;
import com.simplysmart.service.config.ServiceGeneratorV2;
import com.simplysmart.service.endpint.ApiInterface;
import com.simplysmart.service.model.category.CategoryResponse;
import com.simplysmart.service.model.common.APIError;
import com.simplysmart.service.model.common.CommonResponse;
import com.simplysmart.service.model.helpdesk.ComplaintChatRequest;
import com.simplysmart.service.model.helpdesk.ComplaintChatResponse;
import com.simplysmart.service.model.helpdesk.ComplaintDetailResponse;
import com.simplysmart.service.model.helpdesk.ComplaintRequest;
import com.simplysmart.service.model.helpdesk.HelpDeskResponse;
import com.simplysmart.service.model.helpdesk.NewComplaint;
import com.simplysmart.service.model.sensor.SensorList;
import com.simplysmart.service.model.sensor.SensorReadingGraphResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by shekhar on 13/05/16.
 * CreateRequest class offer all the methods to prepare request for network call & sync with server
 */
public class CreateRequest {

    private static CreateRequest createRequest = new CreateRequest();

    protected CreateRequest() {
    }

    public static CreateRequest getInstance() {
        return createRequest;
    }

    public void loadSessionData(String apiKey, String authToken, String subDomain) {
        GlobalData.getInstance().setApi_key(apiKey);
        GlobalData.getInstance().setAuthToken(authToken);
        GlobalData.getInstance().setSubDomain(subDomain);
    }

    //    public void loginRequest(Context context, String packageName, String mobileNumber, String pinNumber, String gcmToken, final ApiCallback<LoginResponse> callback) {
//
//        LoginRequest request = new LoginRequest();
//        LoginRequest.Session session = new LoginRequest.Session();
//        session.setLogin(mobileNumber);
//        session.setPassword(pinNumber);
//        session.setDevice_id(CommonMethod.getDeviceId(context) + packageName);
//        session.setNotification_token(gcmToken);
//        request.setSession(session);
//
//        ApiInterface apiInterface = ServiceGeneratorV2.createService(ApiInterface.class);
//
//        Call<LoginResponse> loginResponseCall = apiInterface.residentLogin(request);
//
//        loginResponseCall.enqueue(new Callback<LoginResponse>() {
//
//            @Override
//            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
//
//                if (response.isSuccessful()) {
//                    callback.onSuccess(response.body());
//                } else {
//                    APIError error = ErrorUtils.parseError(response);
//                    callback.onFailure(error.message());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<LoginResponse> call, Throwable t) {
//
//            }
//        });
//    }
//
//    public void loginRequestWithSubDomain(Context context, String packageName, String subDomain, String mobileNumber, String pinNumber, String gcmToken, final ApiCallback<LoginResponse> callback) {
//
//        LoginRequest request = new LoginRequest();
//        LoginRequest.Session session = new LoginRequest.Session();
//        session.setLogin(mobileNumber);
//        session.setPassword(pinNumber);
//        session.setDevice_id(CommonMethod.getDeviceId(context) + packageName);
//        session.setNotification_token(gcmToken);
//        request.setSession(session);
//
//        ApiInterface apiInterface = ServiceGeneratorV2.createService(ApiInterface.class);
//
//        Call<LoginResponse> loginResponseCall = apiInterface.residentLoginWithSubDomain(subDomain, request);
//
//        loginResponseCall.enqueue(new Callback<LoginResponse>() {
//
//            @Override
//            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
//
//                if (response.isSuccessful()) {
//                    callback.onSuccess(response.body());
//                } else {
//                    APIError error = ErrorUtils.parseError(response);
//                    callback.onFailure(error.message());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<LoginResponse> call, Throwable t) {
//
//            }
//        });
//    }
//
    public void logoutRequestWithSubDomain(final ApiCallback<CommonResponse> callback) {

        ApiInterface apiInterface = ServiceGenerator.createService(ApiInterface.class);

        Call<CommonResponse> loginResponseCall = apiInterface.residentLogout(GlobalData.getInstance().getSubDomain());

        loginResponseCall.enqueue(new Callback<CommonResponse>() {

            @Override
            public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {

                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    APIError error = ErrorUtils.parseError(response);
                    callback.onFailure(error.message());
                }
            }

            @Override
            public void onFailure(Call<CommonResponse> call, Throwable t) {

            }
        });
    }

    //
//    public void changePassword(String residentId, ChangePasswordRequest request, final ApiCallback<CommonResponse> callback) {
//
//        ApiInterface apiInterface = ServiceGenerator.createService(ApiInterface.class);
//        Call<CommonResponse> responseCall = apiInterface.changePassword(residentId, AppSessionData.getInstance().getSubdomain(), request);
//
//        responseCall.enqueue(new Callback<CommonResponse>() {
//
//            @Override
//            public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
//
//                if (response.isSuccessful()) {
//                    callback.onSuccess(response.body());
//                } else {
//                    APIError error = ErrorUtils.parseError(response);
//                    callback.onFailure(error.message());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<CommonResponse> call, Throwable t) {
//
//            }
//        });
//    }
//
//    public void resetPassword(String username, final ApiCallback<LoginResponse> callback) {
//
//        ApiInterface apiInterface = ServiceGenerator.createService(ApiInterface.class);
//        Call<LoginResponse> responseCall = apiInterface.resetPassword(username);
//
//        responseCall.enqueue(new Callback<LoginResponse>() {
//
//            @Override
//            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
//
//                if (response.isSuccessful()) {
//                    callback.onSuccess(response.body());
//                } else {
//                    APIError error = ErrorUtils.parseError(response);
//                    callback.onFailure(error.message());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<LoginResponse> call, Throwable t) {
//
//            }
//        });
//    }
//
    public void fetchCategories(final ApiCallback<CategoryResponse> callback) {

        ApiInterface apiInterface = ServiceGeneratorV2.createService(ApiInterface.class);
        Call<CategoryResponse> responseCall = apiInterface.fetchCategories(GlobalData.getInstance().getSubDomain());

        responseCall.enqueue(new Callback<CategoryResponse>() {

            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {

                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    APIError error = ErrorUtils.parseError(response);
                    callback.onFailure(error.message());
                }
            }

            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {

            }
        });
    }

    //
//    public void fetchCredential(final ApiCallback<CloudinaryCredential> callback) {
//
//        ApiInterface apiInterface = ServiceGenerator.createService(ApiInterface.class);
//        Call<CloudinaryCredential> responseCall = apiInterface.fetchCredential(AppSessionData.getInstance().getSubdomain());
//
//        responseCall.enqueue(new Callback<CloudinaryCredential>() {
//
//            @Override
//            public void onResponse(Call<CloudinaryCredential> call, Response<CloudinaryCredential> response) {
//
//                if (response.isSuccessful()) {
//                    callback.onSuccess(response.body());
//                } else {
//                    APIError error = ErrorUtils.parseError(response);
//                    callback.onFailure(error.message());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<CloudinaryCredential> call, Throwable t) {
//
//            }
//        });
//    }
//
//    public void getNotificationList(String residentId, final ApiCallback<NotificationResponse> callback) {
//
//        ApiInterface apiInterface = ServiceGenerator.createService(ApiInterface.class);
//        Call<NotificationResponse> responseCall = apiInterface.getNotificationList(residentId, AppSessionData.getInstance().getSubdomain());
//
//        responseCall.enqueue(new Callback<NotificationResponse>() {
//
//            @Override
//            public void onResponse(Call<NotificationResponse> call, Response<NotificationResponse> response) {
//
//                if (response.isSuccessful()) {
//                    callback.onSuccess(response.body());
//                } else {
//                    APIError error = ErrorUtils.parseError(response);
//                    callback.onFailure(error.message());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<NotificationResponse> call, Throwable t) {
//
//            }
//        });
//    }
//
//    public void updateNotificationStatus(String residentId, String notificationId, String readAt, final ApiCallback<NotificationResponse> callback) {
//
//        NotificationRequest putRequest = new NotificationRequest();
//        NotificationRequest.Data data = new NotificationRequest.Data();
//        data.setRead_at(readAt);
//        putRequest.setNotification(data);
//
//        ApiInterface apiInterface = ServiceGenerator.createService(ApiInterface.class);
//        Call<NotificationResponse> responseCall = apiInterface.updateNotificationStatus(residentId, notificationId, AppSessionData.getInstance().getSubdomain(), putRequest);
//
//        responseCall.enqueue(new Callback<NotificationResponse>() {
//
//            @Override
//            public void onResponse(Call<NotificationResponse> call, Response<NotificationResponse> response) {
//
//                if (response.isSuccessful()) {
//                    callback.onSuccess(response.body());
//                } else {
//                    APIError error = ErrorUtils.parseError(response);
//                    callback.onFailure(error.message());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<NotificationResponse> call, Throwable t) {
//
//            }
//        });
//    }
//
    public void createNewComplaint(NewComplaint complaint, final ApiCallback<CommonResponse> callback) {

        ComplaintRequest complaintRequest = new ComplaintRequest();
        complaintRequest.setComplaint(complaint);

        ApiInterface apiInterface = ServiceGenerator.createService(ApiInterface.class);
        Call<CommonResponse> responseCall = apiInterface.createNewTicket(GlobalData.getInstance().getSubDomain(), complaintRequest);

        responseCall.enqueue(new Callback<CommonResponse>() {
            @Override
            public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    APIError error = ErrorUtils.parseError(response);
                    callback.onFailure(error.message());
                }
            }

            @Override
            public void onFailure(Call<CommonResponse> call, Throwable t) {

            }
        });
    }

    //
    public void getComplaintList(String state, String page, final ApiCallback<HelpDeskResponse> callback) {

        ApiInterface apiInterface = ServiceGenerator.createService(ApiInterface.class);
        Call<HelpDeskResponse> responseCall = apiInterface.getComplaintsData(GlobalData.getInstance().getSubDomain(), state, page);

        responseCall.enqueue(new Callback<HelpDeskResponse>() {

            @Override
            public void onResponse(Call<HelpDeskResponse> call, Response<HelpDeskResponse> response) {

                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    APIError error = ErrorUtils.parseError(response);
                    callback.onFailure(error.message());
                }
            }

            @Override
            public void onFailure(Call<HelpDeskResponse> call, Throwable t) {

            }
        });
    }

    public void getComplaintDetails(String complaintId, final ApiCallback<ComplaintDetailResponse> callback) {

        ApiInterface apiInterface = ServiceGenerator.createService(ApiInterface.class);
        Call<ComplaintDetailResponse> responseCall = apiInterface.getComplaintDetails(complaintId, GlobalData.getInstance().getSubDomain());

        responseCall.enqueue(new Callback<ComplaintDetailResponse>() {

            @Override
            public void onResponse(Call<ComplaintDetailResponse> call, Response<ComplaintDetailResponse> response) {

                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    APIError error = ErrorUtils.parseError(response);
                    callback.onFailure(error.message());
                }
            }

            @Override
            public void onFailure(Call<ComplaintDetailResponse> call, Throwable t) {

            }
        });
    }

    public void postComment(String complaintId, String comment, String image_url, final ApiCallback<ComplaintChatResponse> callback) {

        ApiInterface apiInterface = ServiceGenerator.createService(ApiInterface.class);

        ComplaintChatRequest request = new ComplaintChatRequest();
        ComplaintChatRequest.Activity activity = new ComplaintChatRequest.Activity();
        activity.setText(comment);
        activity.setImage_url(image_url);
        request.setActivity(activity);

        Call<ComplaintChatResponse> responseCall = apiInterface.postComment(complaintId, GlobalData.getInstance().getSubDomain(), request);

        responseCall.enqueue(new Callback<ComplaintChatResponse>() {

            @Override
            public void onResponse(Call<ComplaintChatResponse> call, Response<ComplaintChatResponse> response) {

                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    APIError error = ErrorUtils.parseError(response);
                    callback.onFailure(error.message());
                }
            }

            @Override
            public void onFailure(Call<ComplaintChatResponse> call, Throwable t) {

            }
        });
    }

    //
//    public void getPlannerDetails(String unitId, final ApiCallback<PlannerResponse> callback) {
//
//        ApiInterface apiInterface = ServiceGenerator.createService(ApiInterface.class);
//        Call<PlannerResponse> responseCall = apiInterface.showPlannerDetails(unitId, AppSessionData.getInstance().getSubdomain());
//
//        responseCall.enqueue(new Callback<PlannerResponse>() {
//
//            @Override
//            public void onResponse(Call<PlannerResponse> call, Response<PlannerResponse> response) {
//
//                if (response.isSuccessful()) {
//                    callback.onSuccess(response.body());
//                } else {
//                    APIError error = ErrorUtils.parseError(response);
//                    callback.onFailure(error.message());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<PlannerResponse> call, Throwable t) {
//
//            }
//        });
//    }
//
//    public void updatePlannerDetails(String unitId, boolean amc, int duration, int water, int electricity, final ApiCallback<PlannerResponse> callback) {
//
//        ApiInterface apiInterface = ServiceGenerator.createService(ApiInterface.class);
//
//        PlannerRequest request = new PlannerRequest();
//        Planner planner = new Planner();
//        planner.setAmc(amc);
//        planner.setDuration(duration);
//        planner.setWater(water);
//        planner.setElectricity(electricity);
//        request.setPlanner(planner);
//
//        Call<PlannerResponse> responseCall = apiInterface.updatePlannerDetails(unitId, AppSessionData.getInstance().getSubdomain(), request);
//
//        responseCall.enqueue(new Callback<PlannerResponse>() {
//
//            @Override
//            public void onResponse(Call<PlannerResponse> call, Response<PlannerResponse> response) {
//
//                if (response.isSuccessful()) {
//                    callback.onSuccess(response.body());
//                } else {
//                    APIError error = ErrorUtils.parseError(response);
//                    callback.onFailure(error.message());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<PlannerResponse> call, Throwable t) {
//
//            }
//        });
//    }
//
//    public void getBotDetails(String unitId, String botType, final ApiCallback<BotResponse> callback) {
//
//        ApiInterface apiInterface = ServiceGenerator.createService(ApiInterface.class);
//        Call<BotResponse> responseCall = apiInterface.getBotDetails(unitId, botType, AppSessionData.getInstance().getSubdomain());
//
//        responseCall.enqueue(new Callback<BotResponse>() {
//
//            @Override
//            public void onResponse(Call<BotResponse> call, Response<BotResponse> response) {
//
//                if (response.isSuccessful()) {
//                    callback.onSuccess(response.body());
//                } else {
//                    APIError error = ErrorUtils.parseError(response);
//                    callback.onFailure(error.message());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<BotResponse> call, Throwable t) {
//
//            }
//        });
//    }
//
//    public void showTrends(String unitId, String botType, int duration, final ApiCallback<BotResponse> callback) {
//
//        ApiInterface apiInterface = ServiceGenerator.createService(ApiInterface.class);
//        Call<BotResponse> responseCall = apiInterface.showTrends(unitId, botType, AppSessionData.getInstance().getSubdomain(), duration);
//
//        responseCall.enqueue(new Callback<BotResponse>() {
//
//            @Override
//            public void onResponse(Call<BotResponse> call, Response<BotResponse> response) {
//
//                if (response.isSuccessful()) {
//                    callback.onSuccess(response.body());
//                } else {
//                    APIError error = ErrorUtils.parseError(response);
//                    callback.onFailure(error.message());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<BotResponse> call, Throwable t) {
//
//            }
//        });
//    }
//
//    public void giveFeedback(String complaint_id, ComplaintFeedbackRequest request, final ApiCallback<ComplaintFeedbackResponse> callback) {
//
//        ApiInterface apiInterface = ServiceGenerator.createService(ApiInterface.class);
//        Call<ComplaintFeedbackResponse> responseCall = apiInterface.doFeedbackComplaint(complaint_id, AppSessionData.getInstance().getSubdomain(), request);
//
//        responseCall.enqueue(new Callback<ComplaintFeedbackResponse>() {
//
//            @Override
//            public void onResponse(Call<ComplaintFeedbackResponse> call, Response<ComplaintFeedbackResponse> response) {
//
//                if (response.isSuccessful()) {
//                    callback.onSuccess(response.body());
//                } else {
//                    APIError error = ErrorUtils.parseError(response);
//                    callback.onFailure(error.message());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ComplaintFeedbackResponse> call, Throwable t) {
//
//            }
//        });
//    }
//
    public void getSensorsReadings(String unitId, String type, final ApiCallback<SensorList> callback) {

        ApiInterface apiInterface = ServiceGenerator.createService(ApiInterface.class);
        Call<SensorList> responseCall = apiInterface.getSensorsReadings(GlobalData.getInstance().getSubDomain(), unitId, type);

        responseCall.enqueue(new Callback<SensorList>() {

            @Override
            public void onResponse(Call<SensorList> call, Response<SensorList> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    APIError error = ErrorUtils.parseError(response);
                    callback.onFailure(error.message());
                }
            }

            @Override
            public void onFailure(Call<SensorList> call, Throwable t) {

            }
        });
    }

    public void getSensorsReadingGraph(String sensorKey, String fromDate, String toDate, String type, final ApiCallback<SensorReadingGraphResponse> callback) {

        ApiInterface apiInterface = ServiceGeneratorV2.createService(ApiInterface.class);
        Call<SensorReadingGraphResponse> responseCall = apiInterface.getReadingsGraph(GlobalData.getInstance().getSubDomain(), sensorKey, fromDate, toDate, type);

        responseCall.enqueue(new Callback<SensorReadingGraphResponse>() {

            @Override
            public void onResponse(Call<SensorReadingGraphResponse> call, Response<SensorReadingGraphResponse> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    APIError error = ErrorUtils.parseError(response);
                    callback.onFailure(error.message());
                }
            }

            @Override
            public void onFailure(Call<SensorReadingGraphResponse> call, Throwable t) {

            }
        });
    }
//
//    public void getTransactionCredentials(WalletCredentialRequest request, final ApiCallback<WalletCredentialResponse> callback) {
//
//        WalletApiInterface apiInterface = ServiceGenerator.createService(WalletApiInterface.class);
//        Call<WalletCredentialResponse> responseCall = apiInterface.getTransactionCredentials(AppSessionData.getInstance().getSubdomain(), request);
//
//        responseCall.enqueue(new Callback<WalletCredentialResponse>() {
//
//            @Override
//            public void onResponse(Call<WalletCredentialResponse> call, Response<WalletCredentialResponse> response) {
//
//                if (response.isSuccessful()) {
//                    callback.onSuccess(response.body());
//                } else {
//                    APIError error = ErrorUtils.parseError(response);
//                    callback.onFailure(error.message());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<WalletCredentialResponse> call, Throwable t) {
//
//            }
//        });
//    }
//
//    public void getTransactions(String residentId, final ApiCallback<TransactionResponse> callback) {
//
//        WalletApiInterface apiInterface = ServiceGenerator.createService(WalletApiInterface.class);
//        Call<TransactionResponse> responseCall = apiInterface.getTransactions(residentId, AppSessionData.getInstance().getSubdomain());
//
//        responseCall.enqueue(new Callback<TransactionResponse>() {
//
//            @Override
//            public void onResponse(Call<TransactionResponse> call, Response<TransactionResponse> response) {
//
//                if (response.isSuccessful()) {
//                    callback.onSuccess(response.body());
//                } else {
//                    APIError error = ErrorUtils.parseError(response);
//                    callback.onFailure(error.message());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<TransactionResponse> call, Throwable t) {
//
//            }
//        });
//    }
//
//    public void updateTransaction(String transactionId, WalletCredentialRequest request, final ApiCallback<TransactionResponse> callback) {
//
//        WalletApiInterface apiInterface = ServiceGenerator.createService(WalletApiInterface.class);
//        Call<TransactionResponse> responseCall = apiInterface.updateTransaction(transactionId, AppSessionData.getInstance().getSubdomain(), request);
//
//        responseCall.enqueue(new Callback<TransactionResponse>() {
//
//            @Override
//            public void onResponse(Call<TransactionResponse> call, Response<TransactionResponse> response) {
//
//                if (response.isSuccessful()) {
//                    callback.onSuccess(response.body());
//                } else {
//                    APIError error = ErrorUtils.parseError(response);
//                    callback.onFailure(error.message());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<TransactionResponse> call, Throwable t) {
//
//            }
//        });
//    }
}
