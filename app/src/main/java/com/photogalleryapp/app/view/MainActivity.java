package com.photogalleryapp.app.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.photogalleryapp.app.util.LocationTracker;
import com.photogalleryapp.app.R;
import com.photogalleryapp.app.presenter.GalleryPresenter;

public class MainActivity extends AppCompatActivity implements GalleryPresenter.View  {
    private int index = 0;
    private GalleryPresenter presenter = null;

    public LocationTracker tracker = new LocationTracker();
    public static Location lastKnown;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Load photos stored in the server
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

        presenter = new GalleryPresenter(this, this);
    }

    public void takePhoto(View v) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            EditText caption = (EditText) findViewById(R.id.etCaption);
            caption.setText("caption");
            lastKnown = tracker.getLocation();
            takePictureIntent = presenter.takePhoto(takePictureIntent, lastKnown);
            startActivityForResult(takePictureIntent, presenter.REQUEST_IMAGE_CAPTURE);
        }
    }

    public void doSearch(View v) {
        Intent intent = new Intent(this, SearchActivity.class);
        lastKnown = tracker.getLocation();
        if(lastKnown != null){
            intent.putExtra("CURR_LONGITUDE", lastKnown.getLongitude() + "");
            intent.putExtra("CURR_LATITUDE", lastKnown.getLatitude() + "");
        }
        startActivityForResult(intent, presenter.SEARCH_ACTIVITY_REQUEST_CODE);
    }

    public void scrollPhotos(View v) {
        int index = 0;

        //when photo scrolls, update photo caption
        EditText newCaption = (EditText)findViewById(R.id.etCaption);
        presenter.updatePhoto(newCaption.getText().toString());

        switch (v.getId()) {
            case R.id.btnPrev:
                index = presenter.handleNavigationInput("ScrollLeft");
                break;
            case R.id.btnNext:
                index = presenter.handleNavigationInput("ScrollRight");
                break;
            default:
                break;
        }
        presenter.getPhotoByIndex(index);
    }


    public void displayPhoto(
        Bitmap photo,
        String caption,
        String timestamp,
        double latitude,
        double longitude)
    {
        ImageView iv_gallery = (ImageView) findViewById(R.id.ivGallery);
        TextView tv_timestamp = (TextView) findViewById(R.id.tvTimestamp);
        EditText et_caption = (EditText) findViewById(R.id.etCaption);
        TextView tv_location = (TextView) findViewById(R.id.tvLocation);
        if (photo == null) {
            iv_gallery.setImageResource(R.mipmap.ic_launcher);
            et_caption.setText("");
            tv_timestamp.setText("");
            tv_location.setText("");
        } else {
            iv_gallery.setImageBitmap(photo);
            et_caption.setText(caption);
            tv_timestamp.setText(timestamp);
            tv_location.setText("(Lat) " + latitude+ "\n(Lng) " + longitude);
        }
    }

    public void showErrorAlert(String errorMsg){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(errorMsg);
        builder.setCancelable(true);

        builder.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder.create();
        alert11.show();
    }

    public boolean withIn50Distance(
            double fileLatitude,
            double fileLongitude,
            double searchLatitude,
            double searchLongitude) {
        float[] result = new float[1];
        Location.distanceBetween(fileLatitude, fileLongitude, searchLatitude, searchLongitude, result);
        boolean isWithin50km = false;

        if (result[0] < 50000) {
            // distance between first and second location is less than 50km
            isWithin50km = true;
        }
        return isWithin50km;
    }

    public void shareImageFile(View v) {
        ImageView iv = (ImageView) findViewById(R.id.ivGallery);
        Drawable mDrawable = iv.getDrawable();
        Bitmap mBitmap = ((BitmapDrawable) mDrawable).getBitmap();
        String photoCaption = presenter.getCurrentPhotoCaption();
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), mBitmap, photoCaption, null);

        Uri uri = Uri.parse(path);
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType("image/jpeg");
        startActivity(Intent.createChooser(shareIntent, null));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            presenter.onReturn(requestCode, resultCode, data);
        }
    }
}