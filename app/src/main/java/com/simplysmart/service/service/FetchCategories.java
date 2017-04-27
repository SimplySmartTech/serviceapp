package com.simplysmart.service.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.simplysmart.service.callback.ApiCallback;
import com.simplysmart.service.model.category.Category;
import com.simplysmart.service.request.CreateRequest;

import java.util.ArrayList;

/**
 * Created by shekhar on 27/4/17.
 */
public class FetchCategories extends IntentService {

    public FetchCategories() {
        super(FetchCategories.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        fetchCategories();
    }

    private void fetchCategories() {

        CreateRequest.getInstance().fetchCategories(new ApiCallback<ArrayList<Category>>() {
            @Override
            public void onSuccess(ArrayList<Category> response) {

                Gson gson = new Gson();
                String data = gson.toJson(response);
                SharedPreferences categoryInfo = getSharedPreferences("CategoryInfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor preferencesEditor = categoryInfo.edit();
                preferencesEditor.putString("category_details", data);
                preferencesEditor.apply();
            }

            @Override
            public void onFailure(String error) {

            }
        });
    }
}
