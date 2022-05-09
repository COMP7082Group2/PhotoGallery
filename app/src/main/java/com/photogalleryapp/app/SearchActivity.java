package com.photogalleryapp.app;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.photogalleryapp.app.view.MainActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        try {
            var currLongitude = getIntent().getStringExtra("CURR_LONGITUDE");
            var currLatitude = getIntent().getStringExtra("CURR_LATITUDE");

            Calendar calendar = Calendar.getInstance();
            DateFormat format = new SimpleDateFormat("yyyy‐MM‐dd");
            Date now = calendar.getTime();
            String todayStr = new SimpleDateFormat("yyyy‐MM‐dd", Locale.getDefault()).format(now);
            Date today = format.parse((String) todayStr);
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            String tomorrowStr = new SimpleDateFormat(
                    "yyyy‐MM‐dd",
                    Locale.getDefault()).format( calendar.getTime());

            Date tomorrow = format.parse((String) tomorrowStr);
            ((EditText) findViewById(R.id.etFromDateTime)).setText(new SimpleDateFormat(
                    "yyyy‐MM‐dd HH:mm:ss", Locale.getDefault()).format(today));
            ((EditText) findViewById(R.id.etToDateTime)).setText(new SimpleDateFormat(
                    "yyyy‐MM‐dd HH:mm:ss", Locale.getDefault()).format(tomorrow));

            //Set the longitude and latitude to the current GPS by default
            ((EditText) findViewById(R.id.etLongitude)).setText(currLongitude);
            ((EditText) findViewById(R.id.etLatitude)).setText(currLatitude);

        } catch (Exception ex) {
        }
    }

    public void cancel(final View v) {
        finish();
    }

    public void go(final View v) {
        Intent i = new Intent(this, MainActivity.class);
        //timestamp fields
        EditText from = (EditText) findViewById(R.id.etFromDateTime);
        EditText to = (EditText) findViewById(R.id.etToDateTime);
        //location fields
        EditText longitude = (EditText) findViewById(R.id.etLongitude);
        EditText latitude = (EditText) findViewById(R.id.etLatitude);
        //keyword field
        EditText keywords = (EditText) findViewById(R.id.etKeywords);


        i.putExtra("STARTTIMESTAMP",
                from.getText() != null ? from.getText().toString() : "");
        i.putExtra("ENDTIMESTAMP",
                to.getText() != null ? to.getText().toString() : "");

        i.putExtra("LONGITUDE",
                longitude.getText() != null ? longitude.getText().toString() : "");
        i.putExtra("LATITUDE",
                latitude.getText() != null ? latitude.getText().toString() : "");

        i.putExtra("KEYWORDS",
                keywords.getText() != null ? keywords.getText().toString() : "");


        setResult(RESULT_OK, i);
        finish();
    }
}

