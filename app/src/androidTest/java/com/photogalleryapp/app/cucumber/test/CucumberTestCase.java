package com.photogalleryapp.app.cucumber.test;

import cucumber.api.CucumberOptions;


@CucumberOptions(features = "com.photogalleryapp.app.assets.features",
        glue = "com.photogalleryapp.app.cucumber.steps",
        tags = {"@e2e", "@smoke"})
@SuppressWarnings("unused")
public class CucumberTestCase {
}
