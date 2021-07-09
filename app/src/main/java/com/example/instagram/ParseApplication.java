package com.example.instagram;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //Register your parse models
        ParseObject.registerSubclass(Post.class);
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("3WV4owBcA7Q1uQFxWQtOE8kivGI5ndH9wI3GfeAE")
                .clientKey("SxEuMR4recbGPSFVr98jMacQTTN2hLErRBjMsgQ2")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
