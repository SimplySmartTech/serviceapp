package com.simplysmart.service.model.helpdesk;

/**
 * Created by chandrashekhar on 24/11/15.
 */
public class ComplaintChatRequest {

    private Activity activity;

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public static class Activity{

    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
}