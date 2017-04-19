package com.simplysmart.service.callback;

/**
 * Created by shekhar on 13/05/16.
 * ApiCallback handle the network call response.
 */
public interface ApiCallback<T> {

    void onSuccess(T response);

    void onFailure(String error);
}
