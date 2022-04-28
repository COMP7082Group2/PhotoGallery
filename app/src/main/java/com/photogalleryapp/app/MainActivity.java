package com.photogalleryapp.app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private int SEARCH_ACTIVITY_REQUEST_CODE = 2;
    String mCurrentPhotoPath;
    private ArrayList<String> photos = null;
    private int index = 0;

    private LocationTracker tracker = new LocationTracker();
    private Location lastKnown;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Load photos stored in the server
        photos = findPhotos(new Date(Long.MIN_VALUE), new Date(), "", "", "");

        if (photos.size() == 0) {
            displayPhoto(null);
        } else {
            displayPhoto(photos.get(index));
        }

        AppCenter.start(getApplication(), "0d845a00-10c7-4351-bcc6-d989912ae356",
                Analytics.class, Crashes.class);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 10);
        }
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 5000, 10, tracker);

    }

    public void takePhoto(View v) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.photogalleryapp.app.fileprovider",
                        photoFile);

                photos.add(photoFile.getPath());
                index = photos.size() - 1;

                EditText et = (EditText) findViewById(R.id.etCaption);
                et.setText("caption");
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private ArrayList<String> findPhotos(Date startTimestamp, Date endTimestamp, String keywords, String latitude, String longitude) {
        File file = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath(), "/Android/data/com.photogalleryapp.app/files/Pictures");

        String currentImage = (photos == null || index >= photos.size()) ? null : photos.get(index);
        ArrayList<String> photos = new ArrayList<String>();
        File[] fList = file.listFiles();

        DateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
        double searchLongitude, searchLatitude;
        //Location searchLocation;

        searchLongitude = (longitude != "") ? Double.parseDouble(longitude) : 0;
        searchLatitude = (latitude != "") ? Double.parseDouble(latitude) : 0;
        //searchLocation = new Location("Search Location");
        //searchLocation.setLongitude(searchLongitude);
        //searchLocation.setLatitude(searchLatitude);


        if (fList != null) {
            try {
                for (File f : fList) {

                    String[] attr = f.getPath().split("_");
                    String fileName = attr[2] + "_" + attr[3];

                    double fileLongitude = 0, fileLatitude = 0;

                    if (attr.length > 4) {
                        fileLatitude = Double.parseDouble(attr[4]);
                    }
                    if (attr.length > 5) {
                        fileLongitude = Double.parseDouble(attr[5].replace(".jpg", ""));
                    }
                    // if keyword coordinates were not specified, add the file to the list.
                    if (longitude == "") {
                        searchLongitude = fileLongitude;
                    }
                    if (latitude == "") {
                        searchLatitude = fileLatitude;
                    }
                    //Location fileLocation = new Location("File Location");
                    //fileLocation.setLongitude(fileLongitude);
                    //fileLocation.setLatitude(fileLatitude);

                    float[] result = new float[1];
                    Location.distanceBetween(fileLatitude, fileLongitude, searchLatitude, searchLongitude, result);
                    boolean within50km = false;

                    if (result[0] < 50000) {
                        // distance between first and second location is less than 50km
                        within50km = true;
                    }
                    //39.256 123.210
                    Date fileDate = format.parse(fileName);

                    if (((startTimestamp == null && endTimestamp == null) ||
                            (fileDate.getTime() >= startTimestamp.getTime() && fileDate.getTime() <= endTimestamp.getTime())
                    ) && (keywords == "" || f.getPath().contains(keywords))
                            && within50km) {
                        if (currentImage != null && f.getPath().compareTo(currentImage) == 0)
                            index = photos.size();

                        photos.add(f.getPath());
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return photos;
    }

    public void doSearch(View v) {
        Intent intent = new Intent(this, SearchActivity.class);
        lastKnown = tracker.getLocation();
        intent.putExtra("CURR_LONGITUDE", lastKnown.getLongitude() + "");
        intent.putExtra("CURR_LATITUDE", lastKnown.getLatitude() + "");
        //startActivity(intent);
        startActivityForResult(intent, SEARCH_ACTIVITY_REQUEST_CODE);
    }

    public void scrollPhotos(View v) {
        String newPath = updatePhoto(photos.get(index), ((EditText) findViewById(R.id.etCaption)).getText().toString());
        photos.set(index, newPath);
        switch (v.getId()) {
            case R.id.btnPrev:
                if (index > 0) {
                    index--;
                } else {
                    index = photos.size() - 1;
                }
                break;
            case R.id.btnNext:
                if (index < (photos.size() - 1)) {
                    index++;
                } else {
                    index = 0;
                }
                break;
            default:
                break;
        }
        displayPhoto(photos.get(index));
    }

    private void displayPhoto(String path) {
        ImageView iv_gallery = (ImageView) findViewById(R.id.ivGallery);
        TextView tv_timestamp = (TextView) findViewById(R.id.tvTimestamp);
        EditText et_caption = (EditText) findViewById(R.id.etCaption);
        TextView tv_location = (TextView) findViewById(R.id.tvLocation);
        if (path == null || path == "") {
            iv_gallery.setImageResource(R.mipmap.ic_launcher);
            et_caption.setText("");
            tv_timestamp.setText("");
            tv_location.setText("");
        } else {
            iv_gallery.setImageBitmap(BitmapFactory.decodeFile(path));
            String[] attr = path.split("_");
            et_caption.setText(attr[1]);

            DateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
            DateFormat dest = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
            try {
                Date d = format.parse(attr[2] + "_" + attr[3]);

                tv_timestamp.setText(dest.format(d));
            } catch (ParseException e) {
                tv_timestamp.setText("Invalid Date");
            }
            if (attr.length > 4)
                tv_location.setText("(Lat) " + attr[4] + "\n(Lng) " + attr[5].replace(".jpg", ""));
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        EditText et = (EditText) findViewById(R.id.etCaption);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        lastKnown = tracker.getLocation();
        String imageFileName = "_caption_" + timeStamp + "_"; //_caption_date_time_latitude_longitude_random#

        if (lastKnown != null) {
            imageFileName += lastKnown.getLatitude() + "_" + lastKnown.getLongitude() + "_";
        }

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private String updatePhoto(String path, String caption) {
        String[] attr = path.split("_");

        if (attr.length >= 4) {
            String newName = attr[0] + "_" + caption + "_" + attr[2] + "_" + attr[3] + "_" + attr[4] + "_" + attr[5];
            if(!newName.endsWith(".jpg"))
                newName = newName + ".jpg";

            File to = new File(newName);
            File from = new File(path);
            from.renameTo(to);
            return to.toString();
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SEARCH_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                //Timestamp
                DateFormat format = new SimpleDateFormat("yyyy‐MM‐dd HH:mm:ss");
                Date startTimestamp , endTimestamp;

                try {
                    String from = (String) data.getStringExtra("STARTTIMESTAMP");
                    String to = (String) data.getStringExtra("ENDTIMESTAMP");
                    startTimestamp = format.parse(from);
                    endTimestamp = format.parse(to);
                } catch (Exception ex) {
                    startTimestamp = null;
                    endTimestamp = null;
                }
                //Location
                String longitude = (String) data.getStringExtra("LONGITUDE");
                String latitude = (String) data.getStringExtra("LATITUDE");


                //Keyword
                String keywords = (String) data.getStringExtra("KEYWORDS");
                index = 0;
                photos = findPhotos(startTimestamp, endTimestamp, keywords, latitude, longitude);

                if (photos.size() == 0) {
                    displayPhoto(null);
                } else {
                    displayPhoto(photos.get(index));
                }
            }
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            ImageView mImageView = (ImageView) findViewById(R.id.ivGallery);
            mImageView.setImageBitmap(BitmapFactory.decodeFile(mCurrentPhotoPath));

            TextView tv_timestamp = (TextView) findViewById(R.id.tvTimestamp);
            TextView tv_location = (TextView) findViewById(R.id.tvLocation);

            photos = findPhotos(new Date(Long.MIN_VALUE), new Date(), "", "", "");
            DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
            tv_timestamp.setText(dateFormat.format(new Date()));
            tv_location.setText("(Lat) " + lastKnown.getLatitude() + "\n(Lng) " + lastKnown.getLongitude());
        }
    }
}