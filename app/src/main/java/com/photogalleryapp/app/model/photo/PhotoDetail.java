package com.photogalleryapp.app.model.photo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PhotoDetail {
    private final SimpleDateFormat TIMESTAMP_SAVE_FORMAT = new SimpleDateFormat("yyyyMMdd_HHmmss");
    public String timeStamp;
    public String caption;
    public double latitude;
    public double longitude;

    public PhotoDetail(String caption, String timeStamp, double latitude, double longitude) {
        this.caption = caption;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timeStamp = timeStamp;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getCaption() {
        return caption;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLongitude() {
        return longitude;
    }


    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getTimeStamp() {
        try {
            Date date = TIMESTAMP_SAVE_FORMAT.parse(this.timeStamp);
            return new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeStamp;
    }

    public String getPhotoFileName() {
        //_caption_date_time_latitude_longitude_random#
        return "_" + this.caption + "_" + this.timeStamp +
                "_" + this.latitude +
                "_" + this.longitude + "_";
    }
}
