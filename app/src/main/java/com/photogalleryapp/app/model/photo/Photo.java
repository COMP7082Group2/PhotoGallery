package com.photogalleryapp.app.model.photo;

import android.graphics.Bitmap;

import java.io.File;

public class Photo {
    private File photoFile;
    private PhotoDetail photodetail;

    public Photo(File photoFile) {
        this.photoFile = photoFile;
    }
    
    public Photo(File photoFile, PhotoDetail photodetail) {
        this.photoFile = photoFile;
        this.photodetail = photodetail;
    }

    public File getPhotoFile() {
        return photoFile;
    }

    public String getPath(){
        return photoFile.getPath();
    }
}



