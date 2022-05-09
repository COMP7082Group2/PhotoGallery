package com.photogalleryapp.app.model.photo;
public class PhotoDetail {
    String timeStamp;
    String caption;
    double latitude;
    double longitude;


    public PhotoDetail(String caption,String timeStamp, double latitude, double longitude) {
        this.caption = caption;
        this.timeStamp = timeStamp;
        this.latitude = latitude;
        this.longitude = longitude;
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
        return timeStamp;
    }


    public String getDefaultPhotoFileName() {
        //_caption_date_time_latitude_longitude_random#
        return this.caption + "_" + this.timeStamp +
                "_" + this.latitude + "_" + this.longitude + "_";
    }
}
