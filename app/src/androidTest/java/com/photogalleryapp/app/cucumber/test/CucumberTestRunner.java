package com.photogalleryapp.app.cucumber.test;

import android.os.Bundle;

import androidx.test.runner.AndroidJUnitRunner;


import com.photogalleryapp.app.BuildConfig;

import cucumber.api.android.CucumberInstrumentationCore;

public class CucumberTestRunner extends AndroidJUnitRunner {
    private final CucumberInstrumentationCore instrumentationCore = new CucumberInstrumentationCore(this);
    @Override
    public void onCreate(Bundle arguments) {
        super.onCreate(arguments);

        instrumentationCore.create(arguments);
        start();
    }
    @Override
    public void onStart() {
        super.onStart();
        waitForIdleSync();
        instrumentationCore.start();
    }
}