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
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.photogalleryapp.app.view.MainActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityKeywordSearchTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        File imgDir = new File(
                Environment.getExternalStorageDirectory().getAbsoluteFile(),
                "/Android/data/com.photogalleryapp.app/files/Pictures");
        imgDir.delete();
        imgDir.mkdirs();

        String log = "49.28373802133578";
        String lat = "-123.1207375";
        String fileNameFormat = "_testKeyword_" + "19930419_101010_" + log + "_"+ lat +".jpg";
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File imageFileName = new File(imgDir, fileNameFormat);
        Bitmap bmp = Bitmap.createBitmap(800, 600, Bitmap.Config.RGB_565);

        bmp.eraseColor(Color.BLUE);

        try (OutputStream os = new FileOutputStream(imageFileName)) {
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, os);
        } catch (Throwable e) {
            System.exit(-1);
        }
    }

    @Test
    public void mainActivityKeywordSearchTest() {
        ViewInteraction materialButton = onView(
                allOf(withId(R.id.btnSearch), withText("SEARCH"),
                        isDisplayed()));
        materialButton.perform(click());

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.etKeywords),
                        isDisplayed()));
        appCompatEditText.perform(replaceText("testKeyword"));


        ViewInteraction appCompatEditText2 = onView(withId(R.id.etFromDateTime));
        appCompatEditText2.perform(replaceText("1990???01???17 05:00:00"));


        ViewInteraction appCompatEditText7 = onView(withId(R.id.etToDateTime));
        appCompatEditText7.perform(replaceText("1995???20???20 06:00:00"));

        ViewInteraction longitudeInteraction = onView(withId(R.id.etLatitude));
        longitudeInteraction.perform(replaceText("49.2827"));

        ViewInteraction latitudeInteraction = onView(withId(R.id.etLongitude));
        latitudeInteraction.perform(replaceText("-123.1207375"));

        ViewInteraction materialButton2 = onView(
                allOf(withId(R.id.go), withText("GO"),
                        isDisplayed()));
        materialButton2.perform(click());

        onView(withId(R.id.etCaption)).check(matches(withText("testKeyword")));
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
