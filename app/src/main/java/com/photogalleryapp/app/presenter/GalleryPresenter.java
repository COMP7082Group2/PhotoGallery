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
import com.photogalleryapp.app.model.search.Search;
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
    private View view;
    private Activity context;
    private PhotoRepository repository;
    private ArrayList<Photo> photos  = null;

    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int SEARCH_ACTIVITY_REQUEST_CODE = 2;

    String mCurrentPhotoPath;

    String FILE_AUTH_PATH  = "com.photogalleryapp.app.fileprovider";

    private int index = 0;

    public GalleryPresenter(Activity context, View view) {
        this.context = context;
        this.view = view;
        repository = new PhotoRepository(context);
        photos = repository.findPhotos();
        if (photos.size() > 0)
            getPhotoByIndex(index);
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

    public void getPhotoByIndex(int index){
        Photo selected = photos.get(index);
        PhotoDetail detail = selected.getPhotoDetail();
        view.displayPhoto(
                selected.getBitmap(),
                detail.getCaption(),
                detail.getTimeStamp(),
                detail.getLatitude(),
                detail.getLongitude());
    }

    public void updatePhoto(String caption) {
        if (caption.isEmpty())
            return;

        Photo selected = photos.get(index);
        PhotoDetail detail = selected.getPhotoDetail();
        String path = selected.getPath();

        String[] attr = path.split("_");

        //name not changed
        String originalCaption = attr[1];
        if (originalCaption.equals(caption))
            return;

        String newName = "_" +
                caption + "_" +
                attr[2] + "_" +
                attr[3] + "_" +
                attr[4] + "_" +
                attr[5] + "_" +
                attr[6];

        detail.setCaption(caption);
        selected.setPhotoDetail(detail);

        if (newName != path){
            if(!newName.endsWith(".jpg"))
                newName = newName + ".jpg";

            File from = new File(path);
            File to = new File(newName);
            if(from.exists())
                from.renameTo(to);

            photos.set(index, selected);
        }
    }
//
//    public ArrayList<Photo> findPhotos(Search search) {
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

    public int scrollLeft() {
        if (index > 0) {
            this.index--;
        }
        return this.index;
    }

    public int scrollRight() {
        if (index < (photos.size() -1)) {
            this.index++;
        }
        return this.index;
    }

    public int search(Search search) {
        // TODO: add datetime search, location
        int result = -1;
        for (Photo photo : photos) {
            PhotoDetail detail = photo.getPhotoDetail();
            if (search.keyword == "" || detail.caption.contains(search.keyword)) {
                result = photos.indexOf(photo);
            }
        }
        return result;
    }

    public void onReturn(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case SEARCH_ACTIVITY_REQUEST_CODE:
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

                double searchLongitude, searchLatitude;

                searchLongitude = (longitude != "") ? Double.parseDouble(longitude) : 0;
                searchLatitude = (latitude != "") ? Double.parseDouble(latitude) : 0;

                //Keyword
                String keywords = (String) data.getStringExtra("KEYWORDS");
                Search search = new Search(startTimestamp, endTimestamp, keywords, searchLatitude, searchLongitude);
                int result = search(search);
                if (result != -1)
                    getPhotoByIndex(result);
                else {
                    // TODO: add alert
                    //no result
                }
                break;
            case REQUEST_IMAGE_CAPTURE:
                //set index to added photo
                index = photos.size()-1;
                photos = repository.findPhotos();
                getPhotoByIndex(index);
                break;
        }
    }

    public int handleNavigationInput(String navigationAction)  {
        switch (navigationAction){
            case "ScrollLeft":
                return scrollLeft();
            case "ScrollRight":
                return scrollRight();
            default:
                return this.index;
        }
    }

    public interface View {
        public void displayPhoto(Bitmap photo, String caption, String timestamp, double latitude, double longitude);
    }
}
