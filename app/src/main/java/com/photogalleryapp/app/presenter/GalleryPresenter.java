package com.photogalleryapp.app.presenter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import com.photogalleryapp.app.model.photo.PhotoDetail;
import com.photogalleryapp.app.model.search.Search;
import com.photogalleryapp.app.model.photo.Photo;
import com.photogalleryapp.app.model.photo.PhotoRepository;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;

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

    @RequiresApi(api = Build.VERSION_CODES.N)
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
            mCurrentPhotoPath = photo.getPhotoFile().getPath();
            Uri photoURI = getPhotoUri(photo);
            photos.add(photo);
            index = photos.size() - 1;

            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        }
        return intent;
    }

    public String getCurrentPhotoCaption(){
        return photos.get(index).getPhotoDetail().getCaption();
    }

    public void getPhotoByIndex(int index){
        Photo selected = photos.get(index);
        PhotoDetail detail = selected.getPhotoDetail();
        view.displayPhoto(
                selected.getPhotoFile().getBitmap(),
                detail.getCaption(),
                detail.getTimeStamp(),
                detail.getLatitude(),
                detail.getLongitude());
    }

    public void updatePhoto(String caption) {
        if (caption.isEmpty())
            return;

        Photo selected = photos.get(index);

        selected = repository.updatePhoto(selected, caption);

        photos.set(index, selected);
    }

    private Uri getPhotoUri(Photo photoFile){
        return  FileProvider.getUriForFile(context,
                FILE_AUTH_PATH,
                photoFile.getPhotoFile().getFile());
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

    // Functional Programming feature
    @RequiresApi(api = Build.VERSION_CODES.N)
    public int search(Search search) {
        int result = -1;
        var found = photos
                .stream()
                .filter(p->p.getPhotoDetail().getCaption().contains(search.keyword) ||
                        ((p.getPhotoDetail().getTimeStampAsDate()).getTime() >= search.startDate.getTime() &&
                         (p.getPhotoDetail().getTimeStampAsDate()).getTime() <= search.endDate.getTime()) ||
                        view.withIn50Distance(p.getPhotoDetail().getLatitude(), p.getPhotoDetail().getLongitude(), search.lat, search.lon))
                .collect(Collectors.toList());

        if (found.size() > 0 )
            result = photos.indexOf(found.get(0));

        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
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

                //Search
                int result = search(search);
                if (result != -1)
                    getPhotoByIndex(result);
                else {
                    view.showErrorAlert("no result");
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
        public void showErrorAlert(String errorMsg);
        public boolean withIn50Distance(double fileLatitude, double fileLongitude, double searchLatitude, double searchLongitude);
    }
}
