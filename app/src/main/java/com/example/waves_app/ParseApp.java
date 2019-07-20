package com.example.waves_app;

import android.app.Application;

import com.parse.Parse;

/* Put these lines back in manifest when using Parse
 * android:name=".ParseApp"
 *
 * Make sure the switch the .MainActivity with .HomeActivity so that the
 * log in page shows up first!!
 *
 * Uncomment the below code regarding registering Parse model too!
 */

public class ParseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // registering Parse model
        // ParseObject.registerSubclass(Category.class);

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
