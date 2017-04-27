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

    public static class Activity {

        private String text;
        private String image_url;

        public String getImage_url() {
            return image_url;
        }

        public void setImage_url(String image_url) {
            this.image_url = image_url;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}