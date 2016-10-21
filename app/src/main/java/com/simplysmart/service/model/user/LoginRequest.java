package com.simplysmart.service.model.user;

/**
 * Created by shekhar on 9/10/15.
 */
public class LoginRequest {

    private Session session;
    private boolean third_party_apps;
    private boolean user_login;

    public boolean isUser_login() {
        return user_login;
    }

    public void setUser_login(boolean user_login) {
        this.user_login = user_login;
    }

    public boolean isThird_party_apps() {
        return third_party_apps;
    }

    public void setThird_party_apps(boolean third_party_apps) {
        this.third_party_apps = third_party_apps;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public static class Session {

        private String login, password, device_id, notification_token;

        public String getDevice_id() {
            return device_id;
        }

        public void setDevice_id(String device_id) {
            this.device_id = device_id;
        }

        public String getNotification_token() {
            return notification_token;
        }

        public void setNotification_token(String notification_token) {
            this.notification_token = notification_token;
        }

        public String getLogin() {
            return login;
        }

        public void setLogin(String login) {
            this.login = login;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
