package com.photogalleryapp.app.cucumber.steps;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Environment;

import androidx.test.espresso.ViewInteraction;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;


import com.photogalleryapp.app.R;
import com.photogalleryapp.app.view.MainActivity;

import org.junit.BeforeClass;
import org.junit.Rule;


import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class LocationSearchDetailsSteps {
    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);
    private Activity activity;

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.ACCESS_FINE_LOCATION");

    @Before("@location_search_details-feature")
    public void setUpBeforeClass() throws Exception {
        activityTestRule.launchActivity(new Intent());
        activity = activityTestRule.getActivity();
        File imgDir = new File(
                Environment.getExternalStorageDirectory().getAbsoluteFile(),
                "/Android/data/com.photogalleryapp.app/files/Pictures");
        imgDir.delete();
        imgDir.mkdirs();

        String log = "49.2827";
        String lat = "-123.1207375";
        String fileNameFormat = "_testLocation_" + "20220419_101010_" + log + "_" + lat + ".jpg";
        File imageFileName = new File(imgDir, fileNameFormat);
        Bitmap bmp = Bitmap.createBitmap(800, 600, Bitmap.Config.RGB_565);

        bmp.eraseColor(Color.GREEN);

        try (OutputStream os = new FileOutputStream(imageFileName)) {
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, os);
        } catch (Throwable e) {
            System.exit(-1);
        }
    }

    ViewInteraction materialButton = onView(allOf(withId(R.id.btnSearch), withText("SEARCH"), isDisplayed()));
    ViewInteraction latitudeInteraction = onView(withId(R.id.etLatitude));
    ViewInteraction longitudeInteraction = onView(withId(R.id.etLongitude));
    ViewInteraction goButton = onView(allOf(withId(R.id.go), withText("GO"), isDisplayed()));

    @Given("^I start the application$")
    public void iStartTheApplication() {
    }

    @When("^I click search button$")
    public void iClickSearchButton() {

        materialButton.perform(click());
    }

    @And("^I click latitude field$")
    public void iClickLatitudeField() {

        latitudeInteraction.perform(click());
    }

    @And("^I enter valid latitude <latitude>$")
    public void iEnterValidLatitudeLatitude() {

        latitudeInteraction.perform(replaceText("49.2827"));
    }

    @And("^I close the keyboard$")
    public void iCloseTheKeyboard() {
        closeSoftKeyboard();
    }

    @And("^I click longitude field$")
    public void iClickLongitudeField() {


        longitudeInteraction.perform(click());
    }

    @And("^I enter valid longitude <longitude>$")
    public void iEnterValidLongitudeLongitude() {

        longitudeInteraction.perform(replaceText("-123.1207375"));
    }

    @And("^I click the go button$")
    public void iClickTheGoButton() {
        goButton.perform(click());
    }

    @Then("^I expect to find the picture$")
    public void iExpectToFindThePicture() {
        onView(withId(R.id.etCaption)).check(matches(withText("testLocation")));
    }
}
