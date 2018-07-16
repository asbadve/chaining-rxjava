package com.murki.flckrdr;

import android.app.Application;

import com.facebook.stetho.Stetho;

public class AppClass extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);

    }
}
