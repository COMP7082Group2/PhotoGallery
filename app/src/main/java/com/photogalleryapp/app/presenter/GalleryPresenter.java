package com.photogalleryapp.app.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.EditText;

import androidx.core.content.FileProvider;

import com.photogalleryapp.app.model.photo.PhotoDetail;
import com.photogalleryapp.app.util.LocationTracker;
import com.photogalleryapp.app.R;
import com.photogalleryapp.app.model.photo.Photo;
import com.photogalleryapp.app.model.photo.PhotoRepository;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class GalleryPresenter {
    private Activity context;
    private PhotoRepository repository;
    private ArrayList<Photo> photos  = null;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    String mCurrentPhotoPath;

    String LOCAL_FILE_PATH = "/Android/data/com.photogalleryapp.app/files/Pictures";
    String FILE_AUTH_PATH  = "com.photogalleryapp.app.fileprovider";

    private int index = 0;

    public GalleryPresenter(Activity context) {
        this.context = context;
        repository = new PhotoRepository(context);

        initPhotos();
    }

    public Intent takePhoto(Intent intent, Location location) {
        Photo photo = repository.create(location);

        if (photo != null) {
            mCurrentPhotoPath = photo.getPath();
            Uri photoURI = getPhotoUri(photo);
            photos.add(photo);
            index = photos.size() - 1;

            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        }
        return intent;
    }

    public int getLastPhotoSize(){
        return photos.size();
    }

    private void initPhotos(){
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), LOCAL_FILE_PATH);
        File[] fList = file.listFiles();
        photos = new ArrayList<Photo>();

        if (fList != null && fList.length > 0) {
            try {
                for (File f : fList) {
                    String[] attr = f.getPath().split("_");
                    double fileLongitude = 0, fileLatitude = 0;

                    if (attr.length > 4)
                        fileLatitude = Double.parseDouble(attr[4]);

                    if (attr.length > 5)
                        fileLongitude = Double.parseDouble(attr[5].replace(".jpg", ""));

                    PhotoDetail detail = new PhotoDetail(attr[2], attr[3], fileLatitude, fileLongitude);
                    photos.add(new Photo(f, detail));
                }
            }
            catch(Exception ex)
            {

            }
        }
    }

    public String getFilePathByIndex(int index){

            return photos.get(index).getPath();

    }

//    public ArrayList<Photo> findPhotos(
//            Date startTimestamp,
//            Date endTimestamp,
//            String keywords,
//            String latitude,
//            String longitude) {
//
//        File file = new File(Environment.getExternalStorageDirectory()
//                .getAbsolutePath(), "");
//
//        Photo currentImage = (photos == null || index >= photos.size()) ? null : photos.get(index);
//        ArrayList<String> photos = new ArrayList<String>();
//        File[] fList = file.listFiles();
//
//        DateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
//        double searchLongitude, searchLatitude;
//        //Location searchLocation;
//
//        searchLongitude = (longitude != "") ? Double.parseDouble(longitude) : 0;
//        searchLatitude = (latitude != "") ? Double.parseDouble(latitude) : 0;
//        //searchLocation = new Location("Search Location");
//        //searchLocation.setLongitude(searchLongitude);
//        //searchLocation.setLatitude(searchLatitude);
//
//
//        if (fList != null && fList.length > 0) {
//            try {
//                for (File f : fList) {
//
//                    String[] attr = f.getPath().split("_");
//                    String fileName = attr[2] + "_" + attr[3];
//
//                    double fileLongitude = 0, fileLatitude = 0;
//
//                    if (attr.length > 4) {
//                        fileLatitude = Double.parseDouble(attr[4]);
//                    }
//                    if (attr.length > 5) {
//                        fileLongitude = Double.parseDouble(attr[5].replace(".jpg", ""));
//                    }
//                    // if keyword coordinates were not specified, add the file to the list.
//                    if (longitude == "") {
//                        searchLongitude = fileLongitude;
//                    }
//                    if (latitude == "") {
//                        searchLatitude = fileLatitude;
//                    }
//                    //Location fileLocation = new Location("File Location");
//                    //fileLocation.setLongitude(fileLongitude);
//                    //fileLocation.setLatitude(fileLatitude);
//
//                    float[] result = new float[1];
//                    Location.distanceBetween(fileLatitude, fileLongitude, searchLatitude, searchLongitude, result);
//                    boolean within50km = false;
//
//                    if (result[0] < 50000) {
//                        // distance between first and second location is less than 50km
//                        within50km = true;
//                    }
//                    //39.256 123.210
//                    Date fileDate = format.parse(fileName);
//
//                    if (((startTimestamp == null && endTimestamp == null) ||
//                            (fileDate.getTime() >= startTimestamp.getTime() && fileDate.getTime() <= endTimestamp.getTime())
//                    ) && (keywords == "" || f.getPath().contains(keywords))
//                            && within50km) {
//                        if (currentImage != null && f.getPath().compareTo(currentImage) == 0)
//                            index = photos.size();
//
//                        photos.add(f.getPath());
//                    }
//                }
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//        }
//        return photos;
//
//    }

    private Uri getPhotoUri(Photo photoFile){
        return  FileProvider.getUriForFile(context,
                FILE_AUTH_PATH,
                photoFile.getPhotoFile());
    }

    public void scrollLeft() {
        if(index > 0) {
            this.index--;
        }
    }

    public void scrollRight() {
        if(index < (photos.size() -1)) {
            this.index++;
        }
    }

    public void search() {  }
    public void onReturn(int requestCode, int resultCode, Intent data) {

    }

    public void handleNavigationInput(String navigationAction)  {
        switch (navigationAction){
            case "ScrollLeft":
                scrollLeft();
            case "ScrollRight":
                scrollRight();
        }
    }

    public interface View {
        public void displayPhoto(Bitmap photo, String caption, String timestamp, boolean isFirst, boolean isLast);
    }
}
