package com.photogalleryapp.app.model.photo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.util.UUID;

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
    public void setPhotoDetail(PhotoDetail photodetail){ this.photodetail = photodetail;}

    public PhotoDetail getPhotoDetail(){ return this.photodetail; }

    public Bitmap getBitmap() { return BitmapFactory.decodeFile(getPath()); }

    public File getPhotoFile() { return photoFile; }

    public String getPath(){
        return photoFile.getPath();
    }
}



