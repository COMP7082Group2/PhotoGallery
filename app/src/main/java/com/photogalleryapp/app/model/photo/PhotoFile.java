package com.photogalleryapp.app.model.photo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.IOException;

public class PhotoFile {
    private File file;

    public PhotoFile(File f){
        file = f;
    }

    public PhotoFile(String name, File directory){
        try {
            file = File.createTempFile(
                    name,  /* prefix */
                    ".jpg",         /* suffix */
                    directory      /* directory */
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File getFile(){
        return file;
    }

    public Bitmap getBitmap() { return BitmapFactory.decodeFile(getPath()); }

    public String getPath(){
        return file.getPath();
    }
}
