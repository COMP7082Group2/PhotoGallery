package com.photogalleryapp.app;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;


import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Environment;

import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.runner.AndroidJUnit4;

import com.photogalleryapp.app.view.MainActivity;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityLocationSearchTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.ACCESS_FINE_LOCATION");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
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

    @Test
    public void mainActivityLocationSearchTest() {
        ViewInteraction materialButton = onView(
                allOf(withId(R.id.btnSearch), withText("SEARCH"),
                        isDisplayed()));
        materialButton.perform(click());

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.etKeywords),
                        isDisplayed()));

        appCompatEditText.perform(replaceText("testLocation"));

        ViewInteraction appCompatEditText2 = onView(withId(R.id.etFromDateTime));
        appCompatEditText2.perform(replaceText("2022‐04‐01 00:00:00"));
        
        
        ViewInteraction appCompatEditText7 = onView(withId(R.id.etToDateTime));
        appCompatEditText7.perform(replaceText("2022-05‐01 00:00:00"));

        ViewInteraction longitudeInteraction = onView(withId(R.id.etLatitude));
        longitudeInteraction.perform(replaceText("49.2827"));

        ViewInteraction latitudeInteraction = onView(withId(R.id.etLongitude));
        latitudeInteraction.perform(replaceText("-123.1207375"));

        ViewInteraction goButton = onView(
                allOf(withId(R.id.go), withText("GO"),
                        isDisplayed()));
        goButton.perform(click());

        onView(withId(R.id.etCaption)).check(matches(withText("testLocation")));
    }
}
