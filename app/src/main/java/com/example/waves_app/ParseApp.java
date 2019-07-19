package com.example.waves_app;

import android.app.Application;

import com.example.waves_app.model.Category;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

         // registering Parse model
         ParseObject.registerSubclass(Category.class);

        // set applicationId, and server based on the values in the Heroku settings
        // clientKey is not needed unless explicitly configured
        // any network interceptors must be added with the Configuration Builder given the syntax
        final Parse.Configuration configuration = new Parse.Configuration.Builder(this)
                .applicationId("johnny-hopkins")
                .clientKey("sloan-kettering")
                .server("https://fbu-waves.herokuapp.com/parse/")
                .build();

        Parse.initialize(configuration);
    }
}
