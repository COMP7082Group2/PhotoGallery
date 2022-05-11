package com.photogalleryapp.app.model.photo;

import android.content.Context;
import android.location.Location;
import android.os.Build;
import android.os.Environment;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

interface IPhotoRepository {
    public Photo create(Location location);
    public ArrayList<Photo> findPhotos();
}

public class PhotoRepository implements IPhotoRepository {
    private Context context;
    private final String LOCAL_FILE_PATH = "/Android/data/com.photogalleryapp.app/files/Pictures";

    public PhotoRepository(Context context) {
        this.context = context;
    }

    @Override
    public Photo create(Location location) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        PhotoDetail detail = new PhotoDetail(
                "caption",
                timeStamp,
                location.getLatitude(),
                location.getLongitude());

        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File file = null;
        try {
            file = File.createTempFile(
                    detail.getPhotoFileName(),  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Photo(file, detail);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public ArrayList<Photo> findPhotos() {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), LOCAL_FILE_PATH);
        File[] fList = file.listFiles();
        ArrayList<Photo> photos = new ArrayList<Photo>();

        if (fList != null && fList.length > 0) {
            try {
                for (File f : fList) {
                    String[] attr = f.getPath().split("_");
                    double fileLongitude = 0, fileLatitude = 0;

                    String date = attr[2] + "_" + attr[3];

                    if (attr.length > 4)
                        fileLatitude = Double.parseDouble(attr[4]);

                    if (attr.length > 5)
                        fileLongitude = Double.parseDouble(attr[5]);

                    String randomNo = attr[6].replace(".jpg", "");

                    PhotoDetail detail = new PhotoDetail(attr[1], date, fileLatitude, fileLongitude);
                    photos.add(new Photo(f, detail));
                }

                Collections.sort(photos, new Comparator<Photo>(){
                    public int compare(Photo o1, Photo o2)
                    {
                        return (o1.getPhotoDetail().getTimeStampAsDate())
                                .compareTo(o2.getPhotoDetail().getTimeStampAsDate());
                    }
                });
            }
            catch (Exception ex)
            {

            }
        }
        return photos;
    }
}

