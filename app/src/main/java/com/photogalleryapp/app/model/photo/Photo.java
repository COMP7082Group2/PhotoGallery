package com.photogalleryapp.app.model.photo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.util.UUID;

public class Photo {
    private PhotoFile photoFile;
    private PhotoDetail photodetail;

    public Photo(PhotoFile photoFile) {
        this.photoFile = photoFile;
    }
    
    public Photo(PhotoFile photoFile, PhotoDetail photodetail) {
        this.photoFile = photoFile;
        this.photodetail = photodetail;
    }
    public void setPhotoDetail(PhotoDetail photodetail){ this.photodetail = photodetail;}

    public PhotoDetail getPhotoDetail(){ return this.photodetail; }

    public PhotoFile getPhotoFile() { return this.photoFile; }


    

}



