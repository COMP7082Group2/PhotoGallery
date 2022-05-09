package com.photogalleryapp.app.model.photo;

import android.content.Context;
import android.location.Location;
import android.os.Environment;
import android.widget.EditText;

import com.photogalleryapp.app.R;
import com.photogalleryapp.app.util.LocationTracker;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

interface IPhotoRepository {
    public Photo create(Location location);
}

public class PhotoRepository implements IPhotoRepository {
    private Context context;

    public PhotoRepository(Context context) {
        this.context = context;
    }

    @Override
    public Photo create(Location location) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        PhotoDetail detail = new PhotoDetail(
                "_caption_",
                timeStamp,
                location.getLatitude(),
                location.getLongitude());

        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File file = null;
        try {
            file = File.createTempFile(
                    detail.getDefaultPhotoFileName(),  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Photo(file, detail);
    }
}

